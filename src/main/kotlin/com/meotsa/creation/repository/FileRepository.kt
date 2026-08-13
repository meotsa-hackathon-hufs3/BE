package com.meotsa.creation.repository

import com.meotsa.creation.entity.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<File, Long> {
    fun findByS3Key(s3Key: String): File?
}
