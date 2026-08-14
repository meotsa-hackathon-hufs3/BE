package com.meotsa.creation.docs

import com.meotsa.creation.dto.response.CreationStartResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Creation", description = "굿즈 만들기 시작 API")
interface CreationSwaggerSpec {
    @Operation(
        summary = "굿즈 만들기 시작",
        description = "빈 생성 작업을 생성하고 이후 단계에서 사용할 `creationId`를 발급한다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "201",
            description = "생성 작업 엔터티 생성 성공",
            content = [
                Content(
                    schema = Schema(implementation = CreationStartResponse::class),
                    examples = [ExampleObject(value = """{"creationId": 1}""")],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun startCreation(): ResponseEntity<CreationStartResponse>
}
