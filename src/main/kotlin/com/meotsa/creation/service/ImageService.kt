package com.meotsa.creation.service

import com.meotsa.creation.dto.request.StyledImageCreateRequest
import com.meotsa.creation.dto.response.StyledImageCreateResponse
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
    fun createStyledImage(
        creationId: Long,
        request: StyledImageCreateRequest,
    ): StyledImageCreateResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        val stylizedImageKey = "StylizedImage_${UUID.randomUUID().toString().take(8)}"
        // TODO: originImageKey 검증
        // TODO: AI API로 StylizedImage 생성
        creation.stylize(request.originalImageKey, stylizedImageKey)

        return StyledImageCreateResponse(
            buildDownloadUrl(creationId, stylizedImageKey),
        )
    }

    @Transactional
    fun retryStyledImage(creationId: Long): StyledImageCreateResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        creation.originalImageKey
            ?: throw BusinessException(CreationErrorCode.STYLIZE_NOT_STARTED)

        val stylizedImageKey = "StylizedImage_${UUID.randomUUID().toString().take(8)}"
        // TODO: originImageKey 검증
        // TODO: AI API로 StylizedImage 생성
        creation.reStylize(stylizedImageKey)

        return StyledImageCreateResponse(
            buildDownloadUrl(creationId, stylizedImageKey),
        )
    }

    private fun buildDownloadUrl(
        creationId: Long,
        key: String,
    ): String = "https://${awsProperties.cloudfront.domain}/creations/$creationId/$key"
}
