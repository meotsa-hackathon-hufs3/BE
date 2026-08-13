package com.meotsa.global.docs

import com.meotsa.global.exception.ErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse

/**
 * 인증이 필요한 엔드포인트의 공통 401 응답.
 * `JWTFilter`·`RestAuthenticationEntryPoint`가 반환하는 케이스
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
    responseCode = "401",
    description = "인증 실패 (토큰 없음·만료·위조)",
    content = [
        Content(
            schema = Schema(implementation = ErrorResponse::class),
            examples = [
                ExampleObject(
                    name = "UNAUTHENTICATED (토큰 없음)",
                    value = """{"code": "UNAUTHENTICATED", "message": "인증이 필요합니다"}""",
                ),
                ExampleObject(
                    name = "EXPIRED_TOKEN (만료)",
                    value = """{"code": "EXPIRED_TOKEN", "message": "만료된 토큰입니다"}""",
                ),
                ExampleObject(
                    name = "INVALID_TOKEN (위조·형식 오류)",
                    value = """{"code": "INVALID_TOKEN", "message": "유효하지 않은 토큰입니다"}""",
                ),
            ],
        ),
    ],
)
annotation class UnauthorizedApiResponse
