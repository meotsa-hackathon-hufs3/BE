package com.meotsa.file.dto.response

data class PresignedUploadResponse(
    val uploadUrl: String,
    val key: String,
    val fileUrl: String,
)
