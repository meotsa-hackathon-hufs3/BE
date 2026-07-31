package com.meotsa.global.jwt

data class TokenInfo(
    val username: String,
    val role: String,
    val category: String,
)
