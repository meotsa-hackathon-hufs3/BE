package com.meotsa.creation.repository

import com.meotsa.creation.entity.Creation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreationRepository : JpaRepository<Creation, Long>
