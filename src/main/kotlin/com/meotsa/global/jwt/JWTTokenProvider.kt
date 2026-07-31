package com.meotsa.global.jwt

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.spec.SecretKeySpec

@Component
class JWTTokenProvider(
    private val jwtProperties: JwtProperties,
) {

    private val secretKey = SecretKeySpec(
        jwtProperties.secret.toByteArray(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().algorithm
    )
    private val parser = Jwts.parser().verifyWith(secretKey).build()

    fun getUsername(token: String): String = claims(token).subject

    fun getRole(token: String): String = claims(token).get("role", String::class.java)

    fun getCategory(token: String): String = claims(token).get("category", String::class.java)

    fun isExpired(token: String) = claims(token).expiration.before(Date())

    fun createAccessToken(username: String, role: String): String =
        createToken(username, role, "access", jwtProperties.accessExpiration)

    fun createRefreshToken(username: String, role: String): String =
        createToken(username, role, "refresh", jwtProperties.refreshExpiration)

    private fun createToken(username: String, role: String, category: String, expiredMs: Long): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .claim("category", category)
            .issuedAt(Date(now))
            .expiration(Date(now + expiredMs))
            .signWith(secretKey)
            .compact()
    }

    private fun claims(token:String) = parser.parseSignedClaims(token).payload
}