package com.meotsa.printshop.docs

import com.meotsa.global.exception.ErrorResponse
import com.meotsa.printshop.dto.response.PrintShopEstimateResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "PrintShop", description = "출력소 견적 API")
interface PrintShopSwaggerSpec {
    @Operation(
        summary = "출력소 견적 목록 조회",
        description =
            "생성 작업에서 선택한 소재로 출력 가능한 출력소들의 견적을 조회한다.",
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = [
                Content(
                    array = ArraySchema(schema = Schema(implementation = PrintShopEstimateResponse::class)),
                    examples = [
                        ExampleObject(
                            value = """
                                [
                                  {
                                    "printShopId": 1,
                                    "name": "메낙스3D",
                                    "tag": "서울·자체 출력소",
                                    "material": "PLA",
                                    "process": "FDM",
                                    "expectedTime": "3~5일",
                                    "minQuantity": 1,
                                    "printingCost": 12000,
                                    "shippingCost": 3000,
                                    "totalCost": 15000
                                  },
                                  {
                                    "printShopId": 2,
                                    "name": "퀵프린트랩",
                                    "tag": "경기·제휴파트너",
                                    "material": "PLA",
                                    "process": "FDM",
                                    "expectedTime": "1~2일",
                                    "minQuantity": 1,
                                    "printingCost": 21000,
                                    "shippingCost": 3500,
                                    "totalCost": 24500
                                  }
                                ]
                            """,
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
            description = "견적에 필요한 모델 정보가 확정되지 않음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "옵션 없음",
                            value = """{"code": "MODEL_OPTION_NOT_READY", "message": "모델 옵션이 확정되지 않아 견적을 낼 수 없습니다"}""",
                        ),
                        ExampleObject(
                            name = "형상 정보 없음",
                            value = """{"code": "MODEL_GEOMETRY_NOT_READY", "message": "모델 형상 정보가 없어 견적을 낼 수 없습니다"}""",
                        ),
                    ],
                ),
            ],
        ),
        ApiResponse(
            responseCode = "500",
            description = "출력소 견적 설정이 공정 산식과 맞지 않음",
            content = [
                Content(
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            value = """{"code": "INVALID_PRINT_SHOP_OPTION", "message": "출력소 견적 설정이 올바르지 않습니다"}""",
                        ),
                    ],
                ),
            ],
        ),
    )
    @SecurityRequirements
    fun getEstimates(
        @Parameter(description = "생성 작업 ID", required = true, example = "1")
        creationId: Long,
    ): ResponseEntity<List<PrintShopEstimateResponse>>
}
