package com.meotsa.creation.dto.request

import jakarta.validation.constraints.NotBlank

data class PresignedUploadRequest(
    @field:NotBlank(message = "파일 이름은 필수입니다")
    val fileName: String,
    @field:NotBlank(message = "콘텐츠 타입은 필수입니다")
    val contentType: String,
)
