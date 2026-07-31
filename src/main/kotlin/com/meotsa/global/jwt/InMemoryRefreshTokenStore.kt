package com.meotsa.global.jwt

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryRefreshTokenStore(
    private val jwtProperties: JwtProperties,
) : RefreshTokenStore {

    private data class StoredToken(val token: String, val expiresAt: Long)

    private val store = ConcurrentHashMap<String, StoredToken>()

    override fun save(username: String, token: String) {
        val expiresAt = System.currentTimeMillis() + jwtProperties.refreshExpiration
        store[username] = StoredToken(token, expiresAt)
    }

    override fun find(username: String): String? {
        val stored = store[username] ?: return null
        if (stored.expiresAt < System.currentTimeMillis()) {
            store.remove(username)
            return null
        }
        return stored.token
    }

    override fun delete(username: String) {
        store.remove(username)
    }
}
