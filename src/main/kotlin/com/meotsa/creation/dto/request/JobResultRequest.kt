package com.meotsa.creation.dto.request

import com.meotsa.creation.entity.JobStatus

data class JobResultRequest(
    val status: JobStatus,
    val s3Key: String? = null,
    val contentType: String? = null,
    val error: String? = null,
)
