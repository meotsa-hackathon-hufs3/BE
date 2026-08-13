package com.meotsa.global.exception

import io.jsonwebtoken.JwtException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // @Valid 검증 실패 (@NotBlank 등)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message =
            e.bindingResult.fieldErrors
                .firstOrNull()
                ?.defaultMessage
                ?: GlobalErrorCode.INVALID_INPUT.message
        return toResponse(GlobalErrorCode.INVALID_INPUT, message)
    }

    // 요청 본문 누락·JSON 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        toResponse(GlobalErrorCode.INVALID_REQUEST_BODY)

    // JWT 파싱/검증 실패 (만료·위조 등)
    @ExceptionHandler(JwtException::class)
    fun handleJwt(e: JwtException): ResponseEntity<ErrorResponse> = toResponse(GlobalErrorCode.INVALID_TOKEN)

    // 로그인 인증 실패 (아이디/비밀번호 불일치 등)
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(e: AuthenticationException): ResponseEntity<ErrorResponse> = toResponse(GlobalErrorCode.INVALID_CREDENTIALS)

    // 비즈니스 예외 (ErrorCode 기반)
    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ErrorResponse> = toResponse(e.errorCode)

    private fun toResponse(
        errorCode: ErrorCode,
        message: String = errorCode.message,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(errorCode.status)
            .body(ErrorResponse(errorCode.code, message))
}
