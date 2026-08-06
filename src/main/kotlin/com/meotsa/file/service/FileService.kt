package com.meotsa.file.service

import com.meotsa.file.dto.request.PresignedUploadRequest
import com.meotsa.file.dto.response.FileUrlResponse
import com.meotsa.file.dto.response.PresignedUploadResponse
import com.meotsa.file.entity.UploadedFile
import com.meotsa.file.exception.FileErrorCode
import com.meotsa.file.repository.FileRepository
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
        val fileUrl = buildFileUrl(key)

        fileRepository.save(
            UploadedFile(
                s3Key = key,
                originalFileName = request.fileName,
                contentType = request.contentType,
                fileUrl = fileUrl,
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
        return FileUrlResponse(file.fileUrl)
    }

    private fun buildFileUrl(key: String): String = "https://${awsProperties.cloudfront.domain}/$key"

    private fun extractExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf('.')
        if (dotIndex <= 0 || dotIndex == fileName.length - 1) {
            throw BusinessException(FileErrorCode.INVALID_FILE_NAME)
        }
        return fileName.substring(dotIndex)
    }
}
