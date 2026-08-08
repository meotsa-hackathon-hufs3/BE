package com.meotsa.file.dto.request

import com.meotsa.file.entity.JobStatus

data class JobResultRequest(
    val status: JobStatus,
    val s3Key: String? = null,
    val contentType: String? = null,
    val error: String? = null,
)
