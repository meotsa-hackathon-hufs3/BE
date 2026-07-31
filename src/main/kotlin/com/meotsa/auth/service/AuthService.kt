package com.meotsa.auth.service

import com.meotsa.auth.dto.LoginRequest
import com.meotsa.auth.dto.RegisterRequest
import com.meotsa.auth.dto.RegisterResponse
import com.meotsa.auth.dto.ReissueRequest
import com.meotsa.auth.dto.TokenResponse
import com.meotsa.auth.exception.AuthErrorCode
import com.meotsa.global.exception.BusinessException
import com.meotsa.global.jwt.JWTTokenProvider
import com.meotsa.global.jwt.RefreshTokenStore
import com.meotsa.user.entity.User
import com.meotsa.user.exception.UserErrorCode
import com.meotsa.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtTokenProvider: JWTTokenProvider,
    val refreshTokenStore: RefreshTokenStore,
    val authenticationManager: AuthenticationManager,
) {

    fun register(request: RegisterRequest): RegisterResponse {

        if (userRepository.existsByUsername(request.username)) {
            throw BusinessException(UserErrorCode.DUPLICATE_USERNAME)
        }

        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
        )
        val savedUser = userRepository.save(user)

        val (accessToken, refreshToken) = issueTokens(savedUser.username, savedUser.role.key)
        return RegisterResponse(savedUser.id!!, accessToken, refreshToken)
    }

    fun login(request: LoginRequest): TokenResponse {

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val username = authentication.name
        val role = authentication.authorities.first().authority

        val (accessToken, refreshToken) = issueTokens(username, role)
        return TokenResponse(accessToken, refreshToken)
    }

    fun reissue(request: ReissueRequest): TokenResponse {

        val refreshToken = request.refreshToken
        validateRefreshToken(refreshToken)

        val username = jwtTokenProvider.getUsername(refreshToken)
        val role = jwtTokenProvider.getRole(refreshToken)

        val (newAccessToken, newRefreshToken) = issueTokens(username, role)
        return TokenResponse(newAccessToken, newRefreshToken)
    }

    fun logout(username: String) {
        refreshTokenStore.delete(username)
    }

    private fun issueTokens(username: String, role: String): Pair<String, String> {
        val accessToken = jwtTokenProvider.createAccessToken(username, role)
        val refreshToken = jwtTokenProvider.createRefreshToken(username, role)
        refreshTokenStore.save(username, refreshToken)
        return accessToken to refreshToken
    }

    private fun validateRefreshToken(refreshToken: String) {
        if (jwtTokenProvider.isExpired(refreshToken)) {
            throw BusinessException(AuthErrorCode.EXPIRED_REFRESH_TOKEN)
        }
        if (jwtTokenProvider.getCategory(refreshToken) != "refresh") {
            throw BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        val username = jwtTokenProvider.getUsername(refreshToken)
        val savedToken = refreshTokenStore.find(username)
            ?: throw BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)
        if (savedToken != refreshToken) {
            throw BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }
    }
}
