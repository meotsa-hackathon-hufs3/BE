package com.meotsa.creation.exception

import com.meotsa.global.exception.ErrorCode
import org.springframework.http.HttpStatus

enum class CreationErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    CREATION_NOT_FOUND(HttpStatus.NOT_FOUND, "생성 작업을 찾을 수 없습니다"),

    STYLIZE_NOT_STARTED(HttpStatus.CONFLICT, "이미지 변환이 시작되지 않았습니다"),
    STYLIZE_NOT_DONE(HttpStatus.CONFLICT, "이미지 변환이 완료되지 않았습니다"),

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "모델 작업을 찾을 수 없습니다"),
    JOB_CREATION_MISMATCH(HttpStatus.NOT_FOUND, "해당 생성 작업에 속한 모델 작업이 아닙니다"),
    JOB_ALREADY_FINISHED(HttpStatus.CONFLICT, "이미 종료된 작업입니다"),
}
