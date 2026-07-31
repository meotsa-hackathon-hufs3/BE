package com.meotsa.global.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.global.exception.ErrorResponse
import com.meotsa.global.security.CustomUserDetails
import com.meotsa.user.entity.Role
import com.meotsa.user.entity.User
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

class JWTFilter(
    private val jwtTokenProvider: JWTTokenProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorization = request.getHeader("Authorization")

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorization.split(" ")[1]

        // 필터는 DispatcherServlet·ExceptionTranslationFilter 앞단이라
        // 여기서 던진 JwtException은 @RestControllerAdvice가 못 잡는다.
        // 따라서 파싱/검증 예외를 직접 잡아 401을 명시적으로 내려준다.
        try {
            if (jwtTokenProvider.getCategory(token) != "access") {
                sendUnauthorized(response, "유효하지 않은 토큰입니다")
                return
            }

            val user =
                User(
                    username = jwtTokenProvider.getUsername(token),
                    password = "temppassword",
                    role = Role.of(jwtTokenProvider.getRole(token)),
                )
            val customUserDetails = CustomUserDetails(user)
            val authToken =
                UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    customUserDetails.authorities,
                )

            SecurityContextHolder.getContext().authentication = authToken
        } catch (e: ExpiredJwtException) {
            sendUnauthorized(response, "만료된 토큰입니다")
            return
        } catch (e: JwtException) {
            sendUnauthorized(response, "유효하지 않은 토큰입니다")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun sendUnauthorized(
        response: HttpServletResponse,
        message: String,
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.writer.write(objectMapper.writeValueAsString(ErrorResponse(message)))
    }
}
