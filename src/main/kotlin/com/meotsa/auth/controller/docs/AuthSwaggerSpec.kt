package com.meotsa.auth.controller.docs

import com.meotsa.auth.dto.request.LoginRequest
import com.meotsa.auth.dto.request.RegisterRequest
import com.meotsa.auth.dto.request.ReissueRequest
import com.meotsa.auth.dto.response.RegisterResponse
import com.meotsa.auth.dto.response.TokenResponse
import com.meotsa.auth.dto.response.UsernameAvailabilityResponse
import com.meotsa.global.docs.UnauthorizedApiResponse
import com.meotsa.global.exception.ErrorResponse
import com.meotsa.global.security.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Auth", description = "인증 API")
interface AuthSwaggerSpec {
    @Operation(
        summary = "회원가입",
        description =
            "아이디·비밀번호로 회원을 생성하고 액세스·리프레시 토큰을 발급한다. " +
                "비밀번호는 8자 이상이며 영문·숫자·특수문자를 각각 1자 이상 포함해야 한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "회원가입 성공"),
        ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패 (아이디·비밀번호 누락, 비밀번호 형식 위반)",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "INVALID_INPUT (아이디 누락)",
                            value = """{"code": "INVALID_INPUT", "message": "아이디는 필수입니다"}""",
                        ),
                        ExampleObject(
                            name = "INVALID_INPUT (비밀번호 누락)",
                            value = """{"code": "INVALID_INPUT", "message": "비밀번호는 필수입니다"}""",
                        ),
                        ExampleObject(
                            name = "INVALID_INPUT (비밀번호 형식 위반)",
                            value = """{"code": "INVALID_INPUT", "message": "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 각각 1자 이상 포함해야 합니다"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 아이디",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(value = """{"code": "DUPLICATE_USERNAME", "message": "이미 존재하는 아이디입니다"}"""),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun register(registerRequest: RegisterRequest): ResponseEntity<RegisterResponse>

    @Operation(
        summary = "아이디 중복 확인",
        description =
            "아이디 사용 가능 여부를 조회한다. 이미 존재하는 아이디여도 200으로 응답하며 `available: false`로 구분한다. ",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [
                Content(
                    schema = Schema(implementation = UsernameAvailabilityResponse::class),
                    examples = [
                        ExampleObject(name = "사용 가능", value = """{"available": true}"""),
                        ExampleObject(name = "이미 존재하는 아이디", value = """{"available": false}"""),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun checkUsernameAvailability(
        @Parameter(description = "중복 확인할 아이디", required = true, example = "test123")
        username: String,
    ): ResponseEntity<UsernameAvailabilityResponse>

    @Operation(
        summary = "로그인",
        description = "아이디·비밀번호를 검증하고 액세스·리프레시 토큰을 발급한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "로그인 성공"),
        ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패 (아이디·비밀번호 누락)",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "INVALID_INPUT (아이디 누락)",
                            value = """{"code": "INVALID_INPUT", "message": "아이디는 필수입니다"}""",
                        ),
                        ExampleObject(
                            name = "INVALID_INPUT (비밀번호 누락)",
                            value = """{"code": "INVALID_INPUT", "message": "비밀번호는 필수입니다"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "401",
            description = "아이디 또는 비밀번호 불일치",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            value = """{"code": "INVALID_CREDENTIALS", "message": "아이디 또는 비밀번호가 올바르지 않습니다"}""",
                        ),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun login(loginRequest: LoginRequest): ResponseEntity<TokenResponse>

    @Operation(
        summary = "토큰 재발급",
        description = "리프레시 토큰을 검증하고 새 액세스·리프레시 토큰을 발급한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "재발급 성공"),
        ApiResponse(
            responseCode = "400",
            description = "입력값 검증 실패",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(value = """{"code": "INVALID_INPUT", "message": "리프레시 토큰은 필수입니다"}""")],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "401",
            description = "리프레시 토큰이 유효하지 않거나 만료됨",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "INVALID_REFRESH_TOKEN (위조·형식 오류)",
                            value = """{"code": "INVALID_REFRESH_TOKEN", "message": "유효하지 않은 리프레시 토큰입니다"}""",
                        ),
                        ExampleObject(
                            name = "EXPIRED_REFRESH_TOKEN (만료)",
                            value = """{"code": "EXPIRED_REFRESH_TOKEN", "message": "만료된 리프레시 토큰입니다"}""",
                        ),
                        ExampleObject(
                            name = "REFRESH_TOKEN_NOT_FOUND (저장된 토큰 없음)",
                            value = """{"code": "REFRESH_TOKEN_NOT_FOUND", "message": "리프레시 토큰을 찾을 수 없습니다"}""",
                        ),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun reissue(reissueRequest: ReissueRequest): ResponseEntity<TokenResponse>

    @Operation(
        summary = "로그아웃",
        description = "저장된 리프레시 토큰을 삭제한다. 헤더에 액세스 토큰이 필요하다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "로그아웃 성공"),
    )
    @UnauthorizedApiResponse
    fun logout(
        @Parameter(hidden = true) userDetails: CustomUserDetails,
    ): ResponseEntity<Void>
}
