package com.meotsa.global.exception

import com.meotsa.user.exception.UserErrorCode
import io.jsonwebtoken.JwtException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
                ?: "잘못된 요청입니다"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message))
    }

    // 비즈니스 예외 (ErrorCode 기반)
    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse(e.errorCode.message))

    // DB 유니크 제약 위반 (동시 가입 레이스 등 사전 체크를 우회한 경우)
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(UserErrorCode.DUPLICATE_USERNAME.status)
            .body(ErrorResponse(UserErrorCode.DUPLICATE_USERNAME.message))

    // JWT 파싱/검증 실패 (만료·위조 등)
    @ExceptionHandler(JwtException::class)
    fun handleJwt(e: JwtException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("유효하지 않은 토큰입니다"))

    // 로그인 인증 실패 (아이디/비밀번호 불일치 등)
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(e: AuthenticationException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("아이디 또는 비밀번호가 올바르지 않습니다"))
}
