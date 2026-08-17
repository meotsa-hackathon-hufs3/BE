package com.meotsa.printshop.exception

import com.meotsa.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class PrintShopErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    MODEL_OPTION_NOT_READY(HttpStatus.CONFLICT, "모델 옵션이 확정되지 않아 견적을 낼 수 없습니다"),
    MODEL_GEOMETRY_NOT_READY(HttpStatus.CONFLICT, "모델 형상 정보가 없어 견적을 낼 수 없습니다"),
    INVALID_PRINT_SHOP_OPTION(HttpStatus.INTERNAL_SERVER_ERROR, "출력소 견적 설정이 올바르지 않습니다"),
}
