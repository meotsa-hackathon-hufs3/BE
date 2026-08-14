package com.meotsa.creation.dto.request

data class StylizedImageCreateRequest(
    val originalImageKey: String,
    val prompt: String?,
)
