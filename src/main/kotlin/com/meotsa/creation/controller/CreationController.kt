package com.meotsa.creation.controller

import com.meotsa.creation.dto.response.CreationStartResponse
import com.meotsa.creation.service.CreationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/creations")
class CreationController(
    private val creationService: CreationService,
) {
    @PostMapping
    fun startCreation(): ResponseEntity<CreationStartResponse> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(creationService.startCreation())
}
