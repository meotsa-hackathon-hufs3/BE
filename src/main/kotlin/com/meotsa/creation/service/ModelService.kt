package com.meotsa.creation.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.meotsa.creation.dto.message.JobMessage
import com.meotsa.creation.dto.request.JobCreateRequest
import com.meotsa.creation.dto.request.JobResultRequest
import com.meotsa.creation.dto.response.JobCreateResponse
import com.meotsa.creation.dto.response.JobStatusResponse
import com.meotsa.creation.entity.Job
import com.meotsa.creation.entity.JobStatus
import com.meotsa.creation.exception.CreationErrorCode
import com.meotsa.creation.exception.FileErrorCode
import com.meotsa.creation.repository.CreationRepository
import com.meotsa.creation.repository.JobRepository
import com.meotsa.global.config.AwsProperties
import com.meotsa.global.exception.BusinessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.sqs.SqsClient

@Service
@Transactional(readOnly = true)
class ModelService(
    private val creationRepository: CreationRepository,
    private val jobRepository: JobRepository,
    private val sqsClient: SqsClient,
    private val awsProperties: AwsProperties,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createJob(
        creationId: Long,
        request: JobCreateRequest,
    ): JobCreateResponse {
        val creation =
            creationRepository.findByIdOrNull(creationId)
                ?: throw BusinessException(CreationErrorCode.CREATION_NOT_FOUND)

        val stylizedImageKey =
            creation.stylizedImageKey
                ?: throw BusinessException(CreationErrorCode.STYLIZE_NOT_DONE)

        val modelOption = request.toModelOption()
        val job = jobRepository.save(Job(creation, modelOption))
        val jobId = job.id!!

        sqsClient.sendMessage {
            it
                .queueUrl(awsProperties.sqs.queueUrl)
                .messageBody(
                    objectMapper.writeValueAsString(
                        JobMessage.of(
                            jobId,
                            creation.id!!,
                            awsProperties.cloudfront.urlOf(stylizedImageKey),
                            modelOption,
                        ),
                    ),
                ).messageGroupId("job")
                .messageDeduplicationId(jobId.toString())
        }

        return JobCreateResponse(jobId)
    }

    fun getJobStatus(
        creationId: Long,
        jobId: Long,
    ): JobStatusResponse {
        val job =
            jobRepository.findByIdOrNull(jobId)
                ?: throw BusinessException(CreationErrorCode.JOB_NOT_FOUND)

        if (job.creation.id != creationId) {
            throw BusinessException(CreationErrorCode.JOB_CREATION_MISMATCH)
        }

        val modelUrl =
            job.modelKey
                ?.takeIf { job.status == JobStatus.COMPLETED }
                ?.let { awsProperties.cloudfront.urlOf(it) }

        return JobStatusResponse.of(job, modelUrl)
    }

    @Transactional
    fun registerJobResult(
        creationId: Long,
        jobId: Long,
        request: JobResultRequest,
    ) {
        val job =
            jobRepository.findByIdOrNull(jobId)
                ?: throw BusinessException(CreationErrorCode.JOB_NOT_FOUND)

        if (job.creation.id != creationId) {
            throw BusinessException(CreationErrorCode.JOB_CREATION_MISMATCH)
        }

        if (job.status != JobStatus.PENDING) {
            throw BusinessException(CreationErrorCode.JOB_ALREADY_FINISHED)
        }

        if (request.status != JobStatus.COMPLETED) {
            job.fail(request.error)
            return
        }

        val modelKey =
            request.modelKey
                ?: throw BusinessException(FileErrorCode.MISSING_RESULT_FILE)

        // Todo: Nullable -> non-null 타입 변환 필요
        job.complete(modelKey, request.structureCheck, request.widthCheck, request.expectedFee)
        job.creation.completeModel(modelKey, job.option, job.expectedFee)
    }
}
