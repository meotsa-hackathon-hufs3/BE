package com.meotsa.creation.docs

import com.meotsa.creation.dto.request.StylizedImageCreateRequest
import com.meotsa.creation.dto.response.StylizedImageResponse
import com.meotsa.global.exception.ErrorResponse
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

@Tag(name = "Image", description = "이미지 변환 API")
interface ImageSwaggerSpec {
    @Operation(
        summary = "이미지 스타일 변환",
        description =
            "업로드한 원본 이미지를 AI로 스타일 변환하고 결과 이미지의 다운로드 URL을 반환한다. " +
                "`originalImageKey`는 사전 업로드 단계에서 발급받은 S3 키를 사용한다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "변환 성공",
            content = [
                Content(
                    schema = Schema(implementation = StylizedImageResponse::class),
                    examples = [
                        ExampleObject(
                            value = """{"stylizedImageUrl": "https://d15xvovmn68oyr.cloudfront.net/creations/1/StylizedImage_6f1c2a3e"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "생성 작업을 찾을 수 없음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(value = """{"code": "CREATION_NOT_FOUND", "message": "생성 작업을 찾을 수 없습니다"}"""),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun createStylizedImage(
        @Parameter(description = "생성 작업 ID", required = true, example = "1")
        creationId: Long,
        request: StylizedImageCreateRequest,
    ): ResponseEntity<StylizedImageResponse>

    @Operation(
        summary = "이미지 스타일 변환 재시도",
        description =
            "직전에 사용한 원본 이미지로 스타일 변환을 다시 수행하고 새 결과 이미지의 다운로드 URL을 반환한다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "재변환 성공",
            content = [
                Content(
                    schema = Schema(implementation = StylizedImageResponse::class),
                    examples = [
                        ExampleObject(
                            value = """{"stylizedImageUrl": "https://d15xvovmn68oyr.cloudfront.net/creations/1/StylizedImage_2c9e5b71"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "생성 작업을 찾을 수 없음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(value = """{"code": "CREATION_NOT_FOUND", "message": "생성 작업을 찾을 수 없습니다"}"""),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "409",
            description = "최초 스타일 변환을 아직 수행하지 않음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(value = """{"code": "STYLIZE_NOT_STARTED", "message": "이미지 변환이 시작되지 않았습니다"}"""),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun retryStylizedImage(
        @Parameter(description = "생성 작업 ID", required = true, example = "1")
        creationId: Long,
    ): ResponseEntity<StylizedImageResponse>
}
