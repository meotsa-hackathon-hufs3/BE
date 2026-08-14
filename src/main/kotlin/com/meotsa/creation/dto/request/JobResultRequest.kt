package com.meotsa.creation.dto.request

import com.meotsa.creation.entity.JobStatus

data class JobResultRequest(
    val status: JobStatus,
    val modelKey: String? = null,
    val structureCheck: Boolean? = null,
    val widthCheck: Boolean? = null,
    val expectedFee: Int? = null,
    val error: String? = null,
)
