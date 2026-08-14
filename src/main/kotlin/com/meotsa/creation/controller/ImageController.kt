package com.meotsa.creation.controller

import com.meotsa.creation.docs.ImageSwaggerSpec
import com.meotsa.creation.dto.request.StylizedImageCreateRequest
import com.meotsa.creation.dto.response.StylizedImageResponse
import com.meotsa.creation.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/creations/{creationId}/stylized-images")
class ImageController(
    private val imageService: ImageService,
) : ImageSwaggerSpec {
    @PostMapping
    override fun createStylizedImage(
        @PathVariable creationId: Long,
        @RequestBody request: StylizedImageCreateRequest,
    ): ResponseEntity<StylizedImageResponse> =
        ResponseEntity
            .ok()
            .body(imageService.createStylizedImage(creationId, request))

    @PostMapping("/retry")
    override fun retryStylizedImage(
        @PathVariable creationId: Long,
    ): ResponseEntity<StylizedImageResponse> =
        ResponseEntity
            .ok()
            .body(imageService.retryStylizedImage(creationId))
}
