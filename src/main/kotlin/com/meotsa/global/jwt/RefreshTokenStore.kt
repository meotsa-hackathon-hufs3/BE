package com.meotsa.global.jwt

interface RefreshTokenStore {
    fun save(
        username: String,
        token: String,
    )

    fun find(username: String): String?

    fun delete(username: String)
}
