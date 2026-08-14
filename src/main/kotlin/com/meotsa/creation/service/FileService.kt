package com.meotsa.creation.service

import com.meotsa.creation.dto.request.PresignedUploadRequest
import com.meotsa.creation.dto.response.FileUrlResponse
import com.meotsa.creation.dto.response.PresignedUploadResponse
import com.meotsa.creation.entity.File
import com.meotsa.creation.exception.FileErrorCode
import com.meotsa.creation.repository.FileRepository
import com.meotsa.global.config.AwsProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
@Transactional(readOnly = true)
class FileService(
    private val s3Presigner: S3Presigner,
    private val awsProperties: AwsProperties,
    private val fileRepository: FileRepository,
) {
    @Transactional
    fun createPresignedUpload(request: PresignedUploadRequest): PresignedUploadResponse {
        val key = "uploads/${UUID.randomUUID()}${extractExtension(request.fileName)}"

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

        // Todo: 사용자 정보 추가
        fileRepository.save(
            File(
                s3Key = key,
                originalFileName = request.fileName,
                contentType = request.contentType,
            ),
        )

        return PresignedUploadResponse(
            uploadUrl = uploadUrl,
            key = key,
            fileUrl = fileUrl,
        )
    }

    fun getFileUrl(key: String): FileUrlResponse {
        val file =
            fileRepository.findByS3Key(key)
                ?: throw BusinessException(FileErrorCode.FILE_NOT_FOUND)
        return FileUrlResponse(awsProperties.cloudfront.urlOf(file.s3Key))
    }

    private fun extractExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        if (dotIndex <= 0 || dotIndex == fileName.length - 1) {
            throw BusinessException(FileErrorCode.INVALID_FILE_NAME)
        }
        return fileName.substring(dotIndex)
    }
}
