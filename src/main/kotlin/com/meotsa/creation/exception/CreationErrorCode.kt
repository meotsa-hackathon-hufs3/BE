package com.meotsa.creation.exception

import com.meotsa.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class CreationErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    CREATION_NOT_FOUND(HttpStatus.NOT_FOUND, "생성 작업을 찾을 수 없습니다"),
    STYLIZE_NOT_STARTED(HttpStatus.CONFLICT, "아직 이미지 변환이 시작되지 않았습니다"),
}
