package com.meotsa.creation.controller

import com.meotsa.creation.dto.request.PresignedUploadRequest
import com.meotsa.creation.dto.response.FileUrlResponse
import com.meotsa.creation.dto.response.PresignedUploadResponse
import com.meotsa.creation.service.FileService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService,
) {
    @PostMapping("/presigned-upload")
    fun createPresignedUpload(
        @Valid @RequestBody request: PresignedUploadRequest,
    ): ResponseEntity<PresignedUploadResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(fileService.createPresignedUpload(request))

    @GetMapping("/download-url")
    fun getDownloadUrl(
        @RequestParam key: String,
    ): ResponseEntity<FileUrlResponse> =
        ResponseEntity
            .ok(fileService.getFileUrl(key))
}
