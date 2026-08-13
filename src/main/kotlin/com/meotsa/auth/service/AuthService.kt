package com.meotsa.auth.service

import com.meotsa.auth.dto.request.LoginRequest
import com.meotsa.auth.dto.request.RegisterRequest
import com.meotsa.auth.dto.request.ReissueRequest
import com.meotsa.auth.dto.response.RegisterResponse
import com.meotsa.auth.dto.response.TokenResponse
import com.meotsa.auth.dto.response.UsernameAvailabilityResponse
import com.meotsa.auth.exception.AuthErrorCode
import com.meotsa.global.exception.BusinessException
import com.meotsa.global.jwt.JWTTokenProvider
import com.meotsa.global.jwt.RefreshTokenStore
import com.meotsa.global.jwt.TokenInfo
import com.meotsa.user.entity.User
import com.meotsa.user.exception.UserErrorCode
import com.meotsa.user.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JWTTokenProvider,
    private val refreshTokenStore: RefreshTokenStore,
    private val authenticationManager: AuthenticationManager,
) {
    @Transactional
    fun register(request: RegisterRequest): RegisterResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw BusinessException(UserErrorCode.DUPLICATE_USERNAME)
        }
        // Todo: 동시 요청 시 existsBy 체크가 뚫리므로 DataIntegrityViolationException 처리 필요

        val user =
            userRepository.save(
                User(
                    username = request.username,
                    password = passwordEncoder.encode(request.password),
                ),
            )

        val (accessToken, refreshToken) = issueTokens(user.username, user.role.key)
        return RegisterResponse(user.id!!, accessToken, refreshToken)
    }

    fun checkUsernameAvailability(username: String) = UsernameAvailabilityResponse(!userRepository.existsByUsername(username))

    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val authentication =
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password),
            )
        val username = authentication.name
        val role = authentication.authorities.first().authority

        val (accessToken, refreshToken) = issueTokens(username, role)
        return TokenResponse(accessToken, refreshToken)
    }

    fun reissue(request: ReissueRequest): TokenResponse {
        val refreshToken = request.refreshToken
        val info = validateRefreshToken(refreshToken)

        val (newAccessToken, newRefreshToken) = issueTokens(info.username, info.role)
        return TokenResponse(newAccessToken, newRefreshToken)
    }

    @Transactional
    fun logout(username: String) {
        refreshTokenStore.delete(username)
    }

    private fun issueTokens(
        username: String,
        role: String,
    ): Pair<String, String> {
        val accessToken = jwtTokenProvider.createAccessToken(username, role)
        val refreshToken = jwtTokenProvider.createRefreshToken(username, role)
        refreshTokenStore.save(username, refreshToken)
        return accessToken to refreshToken
    }

    private fun validateRefreshToken(refreshToken: String): TokenInfo {
        val info =
            try {
                jwtTokenProvider.parse(refreshToken)
            } catch (e: ExpiredJwtException) {
                throw BusinessException(AuthErrorCode.EXPIRED_REFRESH_TOKEN)
            } catch (e: JwtException) {
                throw BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN)
            }
        if (info.category != "refresh") {
            throw BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        val savedToken =
            refreshTokenStore.find(info.username)
                ?: throw BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)
        if (savedToken != refreshToken) {
            throw BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        return info
    }
}
