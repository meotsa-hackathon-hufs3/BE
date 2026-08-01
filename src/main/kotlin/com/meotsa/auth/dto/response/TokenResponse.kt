package com.meotsa.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
