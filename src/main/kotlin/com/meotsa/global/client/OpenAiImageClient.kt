package com.meotsa.global.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.meotsa.creation.exception.CreationErrorCode
import com.meotsa.global.config.OpenAiProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
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

        val response =
            try {
                openAiRestClient
                    .post()
                    .uri("/images/edits")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body.build())
                    .retrieve()
                    .body(ImageEditResponse::class.java)
            } catch (e: RestClientException) {
                throw BusinessException(CreationErrorCode.STYLIZE_FAILED)
            }

        val encoded =
            response?.data?.firstOrNull()?.b64Json
                ?: throw BusinessException(CreationErrorCode.STYLIZE_FAILED)

        return try {
            Base64.getDecoder().decode(encoded)
        } catch (e: IllegalArgumentException) {
            throw BusinessException(CreationErrorCode.STYLIZE_FAILED)
        }
    }

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
}
