package dev.notypie.jwt.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
@Profile("jwt")
class CookieProvider(
    @Value("\${jwt.token.refreshTokenExpiredTime}") private val refreshTokenExpiredTime: String
) {
    private val log = logger()

    fun createRefreshTokenCookie(refreshToken: String): ResponseCookie {
        return ResponseCookie.from("refresh-token", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(this.refreshTokenExpiredTime.toLong())
            .build()
    }

    fun removeRefreshTokenCookie() : ResponseCookie{
        return ResponseCookie.from("refresh-token", "")
            .maxAge(0)
            .path("/")
            .build()
    }
}