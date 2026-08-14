package com.meotsa.creation.service

import com.meotsa.creation.dto.response.CreationStartResponse
import com.meotsa.creation.entity.Creation
import com.meotsa.creation.repository.CreationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CreationService(
    private val creationRepository: CreationRepository,
) {
    @Transactional
    fun startCreation(): CreationStartResponse {
        val creation = creationRepository.save(Creation())

        return CreationStartResponse(creation.id!!)
    }
}
