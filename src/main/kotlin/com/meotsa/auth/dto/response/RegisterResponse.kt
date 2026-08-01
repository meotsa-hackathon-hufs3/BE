package com.meotsa.auth.dto.response

data class RegisterResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
