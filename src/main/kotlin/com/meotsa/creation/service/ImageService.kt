package com.meotsa.creation.service

import com.meotsa.creation.dto.request.StylizedImageCreateRequest
import com.meotsa.creation.dto.request.StylizedImageRetryRequest
import com.meotsa.creation.dto.response.StylizedImageResponse
import com.meotsa.creation.exception.CreationErrorCode
import com.meotsa.creation.repository.CreationRepository
import com.meotsa.global.client.OpenAiImageClient
import com.meotsa.global.config.AwsProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class ImageService(
    private val creationRepository: CreationRepository,
    private val fileService: FileService,
    private val openAiImageClient: OpenAiImageClient,
    private val awsProperties: AwsProperties,
) {
    // AI 호출이 수십 초 걸리므로 트랜잭션(=DB 커넥션) 밖에서 수행한다
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun createStylizedImage(
        creationId: Long,
        request: StylizedImageCreateRequest,
    ): StylizedImageResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        val stylizedImageKey = stylize(creationId, request.originalImageKey, request.prompt)

        creation.stylize(request.originalImageKey, stylizedImageKey)
        creationRepository.save(creation) // 트랜잭션 밖이라 명시적으로 저장

        return StylizedImageResponse(
            awsProperties.cloudfront.urlOf(stylizedImageKey),
        )
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun retryStylizedImage(
        creationId: Long,
        request: StylizedImageRetryRequest,
    ): StylizedImageResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        val originalImageKey =
            creation.originalImageKey
                ?: throw BusinessException(CreationErrorCode.STYLIZE_NOT_STARTED)

        val stylizedImageKey = stylize(creationId, originalImageKey, request.prompt)

        creation.reStylize(stylizedImageKey)
        creationRepository.save(creation)

        return StylizedImageResponse(
            awsProperties.cloudfront.urlOf(stylizedImageKey),
        )
    }

    private fun stylize(
        creationId: Long,
        originalImageKey: String,
        userPrompt: String?,
    ): String {
        val originalImage = fileService.download(originalImageKey)

        val stylizedImage =
            openAiImageClient.editImage(
                image = originalImage,
                filename = originalImageKey.substringAfterLast('/'),
                contentType = contentTypeOf(originalImageKey),
                prompt = buildPrompt(userPrompt),
            )

        val stylizedImageKey = generateStylizedImageKey(creationId)
        fileService.upload(stylizedImageKey, stylizedImage, "image/png")

        return stylizedImageKey
    }

    private fun contentTypeOf(key: String): String =
        when (key.substringAfterLast('.', "").lowercase()) {
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            else -> throw BusinessException(CreationErrorCode.UNSUPPORTED_CONTENT_TYPE)
        }

    private fun generateStylizedImageKey(creationId: Long): String =
        "creations/$creationId/stylized_${UUID.randomUUID().toString().take(8)}.png"

    private fun buildPrompt(userPrompt: String?): String =
        if (userPrompt.isNullOrBlank()) BASE_PROMPT else "$BASE_PROMPT\n\n추가 요청: $userPrompt"

    private companion object {
        const val BASE_PROMPT =
            "입력 사진 속 인물 또는 사물의 정체성과 고유한 특징(얼굴, 헤어스타일, 의상, 색상)을 최대한 유지한 채, " +
                "수집형 3D 피규어 스타일로 변환하라. " +
                "배경은 아무 요소 없는 완전한 단색 흰색으로 채운다. " +
                "3D 프린팅이 가능하도록 형태를 단순화하고, 얇거나 부서지기 쉬운 돌출부는 만들지 않는다. " +
                "그림자, 반사, 텍스트, 워터마크는 넣지 않는다."
    }
}
