package com.meotsa.file.dto.response

data class PresignedUploadResponse(
    val id: Long,
    val uploadUrl: String,
    val key: String,
    val fileUrl: String,
)
