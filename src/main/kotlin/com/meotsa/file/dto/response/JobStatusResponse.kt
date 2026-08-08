package com.meotsa.file.dto.response

import com.meotsa.file.entity.Job
import com.meotsa.file.entity.JobStatus

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
