package com.meotsa.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.global.exception.ErrorCode
import com.meotsa.global.exception.ErrorResponse
import com.meotsa.global.exception.GlobalErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class RestAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        write(response, objectMapper, GlobalErrorCode.UNAUTHENTICATED)
    }

    companion object {
        fun write(
            response: HttpServletResponse,
            objectMapper: ObjectMapper,
            errorCode: ErrorCode,
        ) {
            response.status = errorCode.status.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.writer.write(objectMapper.writeValueAsString(ErrorResponse(errorCode.code, errorCode.message)))
        }
    }
}
