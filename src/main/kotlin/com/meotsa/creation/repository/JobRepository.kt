package com.meotsa.creation.repository

import com.meotsa.creation.entity.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository : JpaRepository<Job, Long>
