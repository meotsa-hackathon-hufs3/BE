package com.meotsa.creation.docs

import com.meotsa.creation.dto.request.JobCreateRequest
import com.meotsa.creation.dto.request.JobResultRequest
import com.meotsa.creation.dto.response.JobCreateResponse
import com.meotsa.creation.dto.response.JobStatusResponse
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

@Tag(name = "Model", description = "3D 모델 생성 API")
interface ModelSwaggerSpec {
    @Operation(
        summary = "3D 모델 생성 요청",
        description =
            "스타일 변환이 완료된 이미지와 선택한 옵션으로 3D 모델 생성 작업을 등록하고 `jobId`를 발급한다. " +
                "실제 생성은 비동기로 진행되므로, 이후 조회 API로 상태를 확인한다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "작업 등록 성공",
            content = [
                Content(
                    schema = Schema(implementation = JobCreateResponse::class),
                    examples = [ExampleObject(value = """{"jobId": 1}""")],
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
            description = "이미지 스타일 변환이 완료되지 않음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(value = """{"code": "STYLIZE_NOT_DONE", "message": "이미지 변환이 완료되지 않았습니다"}"""),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun createJob(
        @Parameter(description = "생성 작업 ID", required = true, example = "1")
        creationId: Long,
        request: JobCreateRequest,
    ): ResponseEntity<JobCreateResponse>

    @Operation(
        summary = "3D 모델 생성 상태 조회",
        description =
            "모델 생성 작업의 진행 상태와 요청 옵션을 조회한다. " +
                "`status`가 `COMPLETED`일 때만 `modelUrl`·`structureCheck`·`widthCheck`·`expectedFee`가 채워지고, " +
                "`FAILED`일 때는 `error`가 채워진다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [
                Content(
                    schema = Schema(implementation = JobStatusResponse::class),
                    examples = [
                        ExampleObject(
                            name = "진행 중",
                            value = """
                                {
                                  "status": "PENDING",
                                  "productType": "FIGURE",
                                  "amount": 1,
                                  "material": "PLA",
                                  "color": "FULL_COLOR"
                                }
                            """,
                        ),
                        ExampleObject(
                            name = "완료",
                            value = """
                                {
                                  "status": "COMPLETED",
                                  "productType": "FIGURE",
                                  "amount": 1,
                                  "material": "PLA",
                                  "color": "FULL_COLOR",
                                  "modelUrl": "https://d15xvovmn68oyr.cloudfront.net/creations/1/model_6f1c2a3e.stl",
                                  "structureCheck": true,
                                  "widthCheck": true,
                                  "expectedFee": 32000
                                }
                            """,
                        ),
                        ExampleObject(
                            name = "실패",
                            value = """
                                {
                                  "status": "FAILED",
                                  "productType": "FIGURE",
                                  "amount": 1,
                                  "material": "PLA",
                                  "color": "FULL_COLOR",
                                  "error": "모델 생성에 실패했습니다"
                                }
                            """,
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "모델 작업을 찾을 수 없거나 해당 생성 작업의 것이 아님",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "작업 없음",
                            value = """{"code": "JOB_NOT_FOUND", "message": "모델 작업을 찾을 수 없습니다"}""",
                        ),
                        ExampleObject(
                            name = "소유 불일치",
                            value = """{"code": "JOB_CREATION_MISMATCH", "message": "해당 생성 작업에 속한 모델 작업이 아닙니다"}""",
                        ),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun getJob(
        @Parameter(description = "생성 작업 ID", required = true, example = "1")
        creationId: Long,
        @Parameter(description = "모델 작업 ID", required = true, example = "1")
        jobId: Long,
    ): ResponseEntity<JobStatusResponse>

    @Operation(
        summary = "3D 모델 생성 결과 등록",
        description =
            "모델 생성 워커가 작업 결과를 콜백으로 등록한다.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "결과 등록 성공"),
        ApiResponse(
            responseCode = "400",
            description = "완료 결과에 모델 키가 없음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            value = """{"code": "MISSING_RESULT_FILE", "message": "완료된 작업에는 결과 파일 정보가 필요합니다"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "404",
            description = "모델 작업을 찾을 수 없거나 해당 생성 작업의 것이 아님",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "작업 없음",
                            value = """{"code": "JOB_NOT_FOUND", "message": "모델 작업을 찾을 수 없습니다"}""",
                        ),
                        ExampleObject(
                            name = "소유 불일치",
                            value = """{"code": "JOB_CREATION_MISMATCH", "message": "해당 생성 작업에 속한 모델 작업이 아닙니다"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 종료된 작업",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(value = """{"code": "JOB_ALREADY_FINISHED", "message": "이미 종료된 작업입니다"}"""),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun registerJobResult(
        @Parameter(description = "생성 작업 ID", required = true, example = "1")
        creationId: Long,
        @Parameter(description = "모델 작업 ID", required = true, example = "1")
        jobId: Long,
        request: JobResultRequest,
    ): ResponseEntity<Void>
}
