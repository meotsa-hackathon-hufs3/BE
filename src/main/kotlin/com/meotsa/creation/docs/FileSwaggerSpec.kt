package com.meotsa.creation.docs

import com.meotsa.creation.dto.request.PresignedUploadRequest
import com.meotsa.creation.dto.response.PresignedUploadResponse
import com.meotsa.global.exception.ErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "File", description = "파일 업로드 API")
interface FileSwaggerSpec {
    @Operation(
        summary = "사전 서명 업로드 URL 발급",
        description =
            "S3에 직접 업로드할 수 있는 사전 서명 URL을 발급한다. " +
                "클라이언트는 `uploadUrl`로 `PUT` 요청을 보내 파일을 업로드하고, " +
                "이후 단계의 API에는 `key`를, 화면 표시·다운로드에는 `fileUrl`을 사용한다. " +
                "`uploadUrl`의 유효 시간은 60분이며, 업로드 시 `Content-Type` 헤더를 요청한 `contentType`과 동일하게 보내야 한다. " +
                "`key`의 확장자는 `contentType`에서 결정되고, 지원하는 값은 " +
                "`image/png`·`image/jpeg`·`model/stl` 이다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "발급 성공",
            content = [
                Content(
                    schema = Schema(implementation = PresignedUploadResponse::class),
                    examples = [
                        ExampleObject(
                            value = """
                                {
                                  "uploadUrl": "https://meotsa-bucket.s3.ap-northeast-2.amazonaws.com/creations/1/original_6f1c2a3e.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Expires=3600&...",
                                  "key": "creations/1/original_6f1c2a3e.png",
                                  "fileUrl": "https://d15xvovmn68oyr.cloudfront.net/creations/1/original_6f1c2a3e.png"
                                }
                            """,
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "400",
            description = "요청 값이 올바르지 않음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "지원하지 않는 contentType",
                            value = """{"code": "UNSUPPORTED_CONTENT_TYPE", "message": "지원하지 않는 파일 형식입니다"}""",
                        ),
                        ExampleObject(
                            name = "본문 파싱 실패 (`purpose`에 정의되지 않은 값 등)",
                            value = """{"code": "INVALID_REQUEST_BODY", "message": "요청 본문이 올바르지 않습니다"}""",
                        ),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun createPresignedUpload(request: PresignedUploadRequest): ResponseEntity<PresignedUploadResponse>
}
