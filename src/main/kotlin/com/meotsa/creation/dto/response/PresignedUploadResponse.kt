package com.meotsa.creation.dto.response

data class PresignedUploadResponse(
    val uploadUrl: String,
    val key: String,
    val fileUrl: String,
)
