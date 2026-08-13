package com.meotsa.creation.dto.response

import com.meotsa.creation.entity.Job
import com.meotsa.creation.entity.JobStatus

data class JobStatusResponse(
    val status: JobStatus,
    val fileUrl3D: String? = null,
    val error: String? = null,
) {
    companion object {
        fun of(
            job: Job,
            fileUrl3D: String?,
        ) = JobStatusResponse(
            status = job.status,
            fileUrl3D = fileUrl3D,
            error = job.error,
        )
    }
}
