package com.meotsa.global.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JWTTokenProvider(
    private val jwtProperties: JwtProperties,
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))
    private val parser = Jwts.parser().verifyWith(secretKey).build()

    fun parse(token: String): TokenInfo {
        val claims = parser.parseSignedClaims(token).payload
        return TokenInfo(
            username = claims.subject,
            role = claims.get("role", String::class.java),
            category = claims.get("category", String::class.java),
        )
    }

    fun createAccessToken(
        username: String,
        role: String,
    ): String = createToken(username, role, "access", jwtProperties.accessExpiration)

    fun createRefreshToken(
        username: String,
        role: String,
    ): String = createToken(username, role, "refresh", jwtProperties.refreshExpiration)

    private fun createToken(
        username: String,
        role: String,
        category: String,
        expiredMs: Long,
    ): String {
        val now = System.currentTimeMillis()
        return Jwts
            .builder()
            .subject(username)
            .claim("role", role)
            .claim("category", category)
            .issuedAt(Date(now))
            .expiration(Date(now + expiredMs))
            .signWith(secretKey)
            .compact()
    }
}
