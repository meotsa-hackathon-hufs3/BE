package com.meotsa.creation.dto.request

import com.meotsa.creation.entity.UploadPurpose

data class PresignedUploadRequest(
    val creationId: Long,
    val purpose: UploadPurpose,
    val contentType: String,
)
