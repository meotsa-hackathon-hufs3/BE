package com.meotsa.file.controller

import com.meotsa.file.dto.request.JobCreateRequest
import com.meotsa.file.dto.request.JobResultRequest
import com.meotsa.file.dto.response.JobCreateResponse
import com.meotsa.file.dto.response.JobStatusResponse
import com.meotsa.file.service.JobService
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
@RequestMapping("/jobs")
class JobController(
    private val jobService: JobService,
) {
    @PostMapping
    fun createJob(
        @Valid @RequestBody request: JobCreateRequest,
    ): ResponseEntity<JobCreateResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(jobService.createJob(request.key))

    @GetMapping("/{jobId}")
    fun getJob(
        @PathVariable("jobId") jobId: Long,
    ): ResponseEntity<JobStatusResponse> =
        ResponseEntity
            .ok()
            .body(jobService.getJobStatus(jobId))

    @PostMapping("/{jobId}/result")
    fun registerJobResult(
        @PathVariable("jobId") jobId: Long,
        @RequestBody request: JobResultRequest,
    ): ResponseEntity<Void> {
        jobService.registerJobResult(jobId, request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build()
    }
}
