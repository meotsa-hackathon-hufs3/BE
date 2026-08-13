package com.meotsa.creation.repository

import com.meotsa.creation.entity.File
import com.meotsa.creation.entity.File3D
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface File3DRepository : JpaRepository<File3D, Long> {
    fun findByImageFile(imageFile: File): File3D?
}
