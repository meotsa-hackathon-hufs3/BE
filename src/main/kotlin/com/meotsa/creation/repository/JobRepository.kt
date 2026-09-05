package com.meotsa.creation.repository

import com.meotsa.creation.entity.Job
import com.meotsa.creation.entity.JobStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository : JpaRepository<Job, Long> {
    fun countByStatusAndIdLessThan(
        status: JobStatus,
        id: Long,
    ): Long
}
