package com.meotsa.file.controller

import com.meotsa.file.dto.request.PresignedUploadRequest
import com.meotsa.file.dto.response.FileUrlResponse
import com.meotsa.file.dto.response.PresignedUploadResponse
import com.meotsa.file.service.FileService
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
@RequestMapping("/files")
class FileController(
    private val fileService: FileService,
) {
    @PostMapping("/presigned-upload")
    fun createPresignedUpload(
        @Valid @RequestBody request: PresignedUploadRequest,
//        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ResponseEntity<PresignedUploadResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(fileService.createPresignedUpload(request))

    @GetMapping("/{id}/download-url")
    fun getDownloadUrl(
        @PathVariable id: Long,
//        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): ResponseEntity<FileUrlResponse> = ResponseEntity.ok(fileService.getFileUrl(id))
}
