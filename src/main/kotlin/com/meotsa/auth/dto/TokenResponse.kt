package com.meotsa.auth.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
