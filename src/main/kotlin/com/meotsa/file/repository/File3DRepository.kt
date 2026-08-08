package com.meotsa.file.repository

import com.meotsa.file.entity.File
import com.meotsa.file.entity.File3D
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface File3DRepository : JpaRepository<File3D, Long> {
    fun findByImageFile(imageFile: File): File3D?
}
