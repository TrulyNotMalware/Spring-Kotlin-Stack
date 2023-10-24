package dev.notypie.jwt.utils

import jakarta.servlet.http.Cookie
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseCookie


@Profile("jwt", "oauth")
object CookieGenerator {
    fun of(responseCookie: ResponseCookie): Cookie {
        val cookie = Cookie(responseCookie.name, responseCookie.value)
        cookie.path = responseCookie.path
        cookie.secure = responseCookie.isSecure
        cookie.isHttpOnly = responseCookie.isHttpOnly
        cookie.maxAge = responseCookie.maxAge.seconds.toInt()
        return cookie
    }
}

