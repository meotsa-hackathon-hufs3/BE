package com.meotsa.global.exception

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
