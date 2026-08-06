package com.meotsa.file.repository

import com.meotsa.file.entity.UploadedFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<UploadedFile, Long> {
    fun findByS3Key(s3Key: String): UploadedFile?
}
