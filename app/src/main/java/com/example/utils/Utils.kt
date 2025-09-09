package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object Utils {
    private const val SECRET = "Media@#$2025" // giống Postman
    private const val ISSUER = "your-issuer"
    private const val SUBJECT = "user-id"
    private const val AUDIENCE = "your-audience"

    fun createJwt(): String {
        val algorithm = Algorithm.HMAC256(SECRET)
        val now = Date()
        val oneHourLater = Date(now.time + 60 * 60 * 1000)

        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(SUBJECT)
            .withAudience(AUDIENCE)
            .withIssuedAt(now)
            .withExpiresAt(oneHourLater)
            .sign(algorithm)
    }
}