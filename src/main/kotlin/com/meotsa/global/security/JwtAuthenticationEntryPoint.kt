package com.meotsa.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.global.exception.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    // 토큰 없이 보호 경로 접근 등 Security가 자동으로 인증을 요구하는 경우.
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        write(response, objectMapper, "인증이 필요합니다")
    }

    companion object {
        fun write(
            response: HttpServletResponse,
            objectMapper: ObjectMapper,
            message: String,
        ) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.writer.write(objectMapper.writeValueAsString(ErrorResponse(message)))
        }
    }
}
