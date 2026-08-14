package com.meotsa.creation.service

import com.meotsa.creation.dto.request.StylizedImageCreateRequest
import com.meotsa.creation.dto.response.StylizedImageResponse
import com.meotsa.creation.exception.CreationErrorCode
import com.meotsa.creation.repository.CreationRepository
import com.meotsa.global.config.AwsProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ImageService(
    private val creationRepository: CreationRepository,
    private val awsProperties: AwsProperties,
) {
    @Transactional
    fun createStylizedImage(
        creationId: Long,
        request: StylizedImageCreateRequest,
    ): StylizedImageResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        val stylizedImageKey = generateStylizedImageKey(creationId)
        // TODO: AI API로 StylizedImage 생성
        creation.stylize(request.originalImageKey, stylizedImageKey)

        return StylizedImageResponse(
            awsProperties.cloudfront.urlOf(stylizedImageKey),
        )
    }

    @Transactional
    fun retryStylizedImage(creationId: Long): StylizedImageResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        creation.originalImageKey
            ?: throw BusinessException(CreationErrorCode.STYLIZE_NOT_STARTED)

        val stylizedImageKey = generateStylizedImageKey(creationId)
        // TODO: AI API로 StylizedImage 생성
        creation.reStylize(stylizedImageKey)

        return StylizedImageResponse(
            awsProperties.cloudfront.urlOf(stylizedImageKey),
        )
    }

    private fun generateStylizedImageKey(creationId: Long): String =
        "creations/$creationId/StylizedImage_${UUID.randomUUID().toString().take(8)}"
}
