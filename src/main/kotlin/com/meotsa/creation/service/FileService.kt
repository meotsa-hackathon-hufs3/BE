package com.meotsa.creation.service

import com.meotsa.creation.dto.request.PresignedUploadRequest
import com.meotsa.creation.dto.response.PresignedUploadResponse
import com.meotsa.creation.entity.UploadPurpose
import com.meotsa.creation.exception.CreationErrorCode
import com.meotsa.global.config.AwsProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FileService(
    private val s3Presigner: S3Presigner,
    private val s3Client: S3Client,
    private val awsProperties: AwsProperties,
) {
    fun createPresignedUpload(request: PresignedUploadRequest): PresignedUploadResponse {
        val prefix =
            when (request.purpose) {
                UploadPurpose.ORIGINAL_IMAGE -> "original"
                UploadPurpose.STYLIZED_IMAGE -> "stylized"
                UploadPurpose.MODEL -> "model"
            }

        val suffix = UUID.randomUUID().toString().take(8)
        val extension = extractExtension(request.contentType)
        val key = "creations/${request.creationId}/${prefix}_$suffix$extension"

        val putObjectRequest =
            PutObjectRequest
                .builder()
                .bucket(awsProperties.s3.bucket)
                .key(key)
                .contentType(request.contentType)
                .build()

        val presignRequest =
            PutObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(60))
                .putObjectRequest(putObjectRequest)
                .build()

        val uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString()
        val fileUrl = awsProperties.cloudfront.urlOf(key)

        return PresignedUploadResponse(
            uploadUrl = uploadUrl,
            key = key,
            fileUrl = fileUrl,
        )
    }

    fun download(key: String): ByteArray {
        val request =
            GetObjectRequest
                .builder()
                .bucket(awsProperties.s3.bucket)
                .key(key)
                .build()

        return s3Client.getObjectAsBytes(request).asByteArray()
    }

    fun upload(
        key: String,
        bytes: ByteArray,
        contentType: String,
    ) {
        val request =
            PutObjectRequest
                .builder()
                .bucket(awsProperties.s3.bucket)
                .key(key)
                .contentType(contentType)
                .build()

        s3Client.putObject(request, RequestBody.fromBytes(bytes))
    }

    private fun extractExtension(contentType: String): String =
        when (contentType.lowercase()) {
            "image/png" -> ".png"
            "image/jpeg" -> ".jpg"
            "model/stl" -> ".stl"
            else -> throw BusinessException(CreationErrorCode.UNSUPPORTED_CONTENT_TYPE)
        }
}
