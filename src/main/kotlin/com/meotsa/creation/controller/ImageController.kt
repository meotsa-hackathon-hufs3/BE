package com.meotsa.creation.controller

import com.meotsa.creation.docs.ImageSwaggerSpec
import com.meotsa.creation.dto.request.StyledImageCreateRequest
import com.meotsa.creation.dto.response.StyledImageCreateResponse
import com.meotsa.creation.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/creations/{creationId}")
class ImageController(
    private val imageService: ImageService,
) : ImageSwaggerSpec {
    @PostMapping("/styled-image")
    override fun createStyledImage(
        @PathVariable creationId: Long,
        @RequestBody request: StyledImageCreateRequest,
    ): ResponseEntity<StyledImageCreateResponse> =
        ResponseEntity
            .ok()
            .body(imageService.createStyledImage(creationId, request))

    @PostMapping("/styled-image/retry")
    override fun retryStyledImage(
        @PathVariable creationId: Long,
    ): ResponseEntity<StyledImageCreateResponse> =
        ResponseEntity
            .ok()
            .body(imageService.retryStyledImage(creationId))
}
