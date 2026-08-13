package com.meotsa.creation.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.creation.dto.message.JobMessage
import com.meotsa.creation.dto.request.JobResultRequest
import com.meotsa.creation.dto.response.JobCreateResponse
import com.meotsa.creation.dto.response.JobStatusResponse
import com.meotsa.creation.entity.File3D
import com.meotsa.creation.entity.Job
import com.meotsa.creation.entity.JobStatus
import com.meotsa.creation.exception.FileErrorCode
import com.meotsa.creation.repository.File3DRepository
import com.meotsa.creation.repository.FileRepository
import com.meotsa.creation.repository.JobRepository
import com.meotsa.global.config.AwsProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.sqs.SqsClient

@Service
@Transactional(readOnly = true)
class JobService(
    private val fileRepository: FileRepository,
    private val jobRepository: JobRepository,
    private val file3DRepository: File3DRepository,
    private val sqsClient: SqsClient,
    private val awsProperties: AwsProperties,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createJob(key: String): JobCreateResponse {
        val imageFile =
            fileRepository.findByS3Key(key)
                ?: throw BusinessException(FileErrorCode.FILE_NOT_FOUND)

        // Todo: 사용자 정보 추가
        val job = jobRepository.save(Job(imageFile = imageFile))
        val jobId = job.id!!

        // Todo: 데모 때는 admin만 열어두기
        sqsClient.sendMessage {
            it
                .queueUrl(awsProperties.sqs.queueUrl)
                .messageBody(objectMapper.writeValueAsString(JobMessage(jobId, key)))
                .messageGroupId("job")
                .messageDeduplicationId(jobId.toString())
        }

        return JobCreateResponse(jobId)
    }

    fun getJobStatus(jobId: Long): JobStatusResponse {
        val job =
            jobRepository.findByIdOrNull(jobId)
                ?: throw BusinessException(FileErrorCode.JOB_NOT_FOUND)
        val fileUrl3D =
            if (job.status == JobStatus.COMPLETED) {
                val file3D =
                    file3DRepository.findByImageFile(job.imageFile)
                        ?: throw BusinessException(FileErrorCode.FILE3D_NOT_FOUND)
                "https://${awsProperties.cloudfront.domain}/${file3D.s3Key}"
            } else {
                null
            }

        return JobStatusResponse.of(job, fileUrl3D)
    }

    @Transactional
    fun registerJobResult(
        jobId: Long,
        request: JobResultRequest,
    ) {
        val job =
            jobRepository.findByIdOrNull(jobId)
                ?: throw BusinessException(FileErrorCode.JOB_NOT_FOUND)

        if (request.status == JobStatus.COMPLETED) {
            val s3Key =
                request.s3Key
                    ?: throw BusinessException(FileErrorCode.MISSING_RESULT_FILE)
            val contentType =
                request.contentType
                    ?: throw BusinessException(FileErrorCode.MISSING_RESULT_FILE)

            job.status = JobStatus.COMPLETED

            file3DRepository.save(
                File3D(
                    imageFile = job.imageFile,
                    s3Key = s3Key,
                    contentType = contentType,
                ),
            )
        } else {
            job.status = JobStatus.FAILED
            job.error = request.error
        }
    }
}
