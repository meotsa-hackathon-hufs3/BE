package com.meotsa.creation.controller

import com.meotsa.creation.docs.FileSwaggerSpec
import com.meotsa.creation.dto.request.PresignedUploadRequest
import com.meotsa.creation.dto.response.PresignedUploadResponse
import com.meotsa.creation.service.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService,
) : FileSwaggerSpec {
    @PostMapping("/presigned-upload")
    override fun createPresignedUpload(
        @RequestBody request: PresignedUploadRequest,
    ): ResponseEntity<PresignedUploadResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(fileService.createPresignedUpload(request))
}
