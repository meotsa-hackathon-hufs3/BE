package com.meotsa.file.exception

import com.meotsa.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class FileErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 이름입니다"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다"),
}
