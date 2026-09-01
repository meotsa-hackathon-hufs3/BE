package com.meotsa.global.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.meotsa.global.config.OpenAiProperties
import com.meotsa.global.exception.BusinessException
import com.meotsa.global.exception.GlobalErrorCode
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
import java.util.Base64

@Component
class OpenAiImageClient(
    private val openAiRestClient: RestClient,
    private val openAiProperties: OpenAiProperties,
) {
    fun editImage(
        image: ByteArray,
        filename: String,
        contentType: String,
        prompt: String,
    ): ByteArray {
        val body = MultipartBodyBuilder()
        body.part("model", openAiProperties.model)
        body.part("prompt", prompt)
        body.part("size", openAiProperties.size)
        body.part("quality", openAiProperties.quality)
        body.part("output_format", "png")
        body.part("n", "1")
        body
            .part("image[]", namedResource(image, filename))
            .header("Content-Type", contentType)

        val startedAt = System.currentTimeMillis()
        val response =
            try {
                openAiRestClient
                    .post()
                    .uri("/images/edits")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body.build())
                    .retrieve()
                    .body(ImageEditResponse::class.java)
            } catch (e: RestClientResponseException) {
                // OpenAI가 내려준 상태 코드·본문을 그대로 남긴다 (429 rate limit, 400 content policy 등 구분용)
                log.error(
                    "OpenAI 이미지 편집 실패 status={} requestId={} elapsed={}ms {} body={}",
                    e.statusCode.value(),
                    e.responseHeaders?.getFirst(HEADER_REQUEST_ID),
                    System.currentTimeMillis() - startedAt,
                    requestSummary(filename, contentType, image.size, prompt),
                    e.responseBodyAsString.take(MAX_LOGGED_BODY_LENGTH),
                )
                throw BusinessException(GlobalErrorCode.IMAGE_EDIT_FAILED)
            } catch (e: RestClientException) {
                // 타임아웃·커넥션 실패 등 응답 자체를 못 받은 경우
                log.error(
                    "OpenAI 이미지 편집 호출 실패 elapsed={}ms timeout={} {}",
                    System.currentTimeMillis() - startedAt,
                    openAiProperties.timeout,
                    requestSummary(filename, contentType, image.size, prompt),
                    e,
                )
                throw BusinessException(GlobalErrorCode.IMAGE_EDIT_FAILED)
            }

        val encoded = response?.data?.firstOrNull()?.b64Json
        if (encoded == null) {
            log.error(
                "OpenAI 이미지 편집 응답에 b64_json 없음 elapsed={}ms dataSize={} {}",
                System.currentTimeMillis() - startedAt,
                response?.data?.size,
                requestSummary(filename, contentType, image.size, prompt),
            )
            throw BusinessException(GlobalErrorCode.IMAGE_EDIT_FAILED)
        }

        return try {
            Base64.getDecoder().decode(encoded).also {
                log.info(
                    "OpenAI 이미지 편집 성공 elapsed={}ms resultBytes={} {}",
                    System.currentTimeMillis() - startedAt,
                    it.size,
                    requestSummary(filename, contentType, image.size, prompt),
                )
            }
        } catch (e: IllegalArgumentException) {
            log.error(
                "OpenAI 이미지 편집 응답 Base64 디코딩 실패 encodedLength={} {}",
                encoded.length,
                requestSummary(filename, contentType, image.size, prompt),
                e,
            )
            throw BusinessException(GlobalErrorCode.IMAGE_EDIT_FAILED)
        }
    }

    private fun requestSummary(
        filename: String,
        contentType: String,
        imageBytes: Int,
        prompt: String,
    ): String =
        "model=${openAiProperties.model} size=${openAiProperties.size} quality=${openAiProperties.quality} " +
            "file=$filename($contentType, $imageBytes bytes) promptLength=${prompt.length}"

    private fun namedResource(
        image: ByteArray,
        filename: String,
    ): ByteArrayResource =
        object : ByteArrayResource(image) {
            override fun getFilename() = filename
        }

    data class ImageEditResponse(
        val data: List<ImageData>?,
    )

    data class ImageData(
        @param:JsonProperty("b64_json")
        val b64Json: String?,
    )

    companion object {
        private val log = LoggerFactory.getLogger(OpenAiImageClient::class.java)
        private const val HEADER_REQUEST_ID = "x-request-id"
        private const val MAX_LOGGED_BODY_LENGTH = 2000
    }
}
