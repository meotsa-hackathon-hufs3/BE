package com.meotsa.auth.dto

data class RegisterResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
)
