package com.meotsa.creation.controller

import com.meotsa.creation.docs.ModelSwaggerSpec
import com.meotsa.creation.dto.request.JobCreateRequest
import com.meotsa.creation.dto.request.JobResultRequest
import com.meotsa.creation.dto.response.JobCreateResponse
import com.meotsa.creation.dto.response.JobStatusResponse
import com.meotsa.creation.service.ModelService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/creations/{creationId}/models")
class ModelController(
    private val modelService: ModelService,
) : ModelSwaggerSpec {
    // Todo: 데모 때는 admin만 열어두기
    @PostMapping
    override fun createJob(
        @PathVariable creationId: Long,
        @Valid @RequestBody request: JobCreateRequest,
    ): ResponseEntity<JobCreateResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(modelService.createJob(creationId, request))

    @GetMapping("/{jobId}")
    override fun getJob(
        @PathVariable creationId: Long,
        @PathVariable jobId: Long,
    ): ResponseEntity<JobStatusResponse> =
        ResponseEntity
            .ok()
            .body(modelService.getJobStatus(creationId, jobId))

    @PostMapping("/{jobId}/result")
    override fun registerJobResult(
        @PathVariable creationId: Long,
        @PathVariable jobId: Long,
        @RequestBody request: JobResultRequest,
    ): ResponseEntity<Void> {
        modelService.registerJobResult(creationId, jobId, request)
        return ResponseEntity
            .ok()
            .build()
    }
}
