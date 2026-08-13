package com.meotsa.creation.exception

import com.meotsa.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class FileErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 이름입니다"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다"),

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "작업을 찾을 수 없습니다"),

    FILE3D_NOT_FOUND(HttpStatus.NOT_FOUND, "3D 파일을 찾을 수 없습니다"),

    MISSING_RESULT_FILE(HttpStatus.BAD_REQUEST, "완료된 작업에는 결과 파일 정보가 필요합니다"),
}
