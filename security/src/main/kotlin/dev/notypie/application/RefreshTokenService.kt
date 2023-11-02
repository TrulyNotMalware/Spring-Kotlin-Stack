package dev.notypie.application

import dev.notypie.domain.Users
import dev.notypie.jwt.dto.JwtDto
import org.springframework.http.ResponseCookie

interface RefreshTokenService {
    fun updateRefreshToken(id: Long, refreshToken: String?)
    fun isDuplicateRefreshToken(id: Long): Boolean
    fun refreshJwtToken(accessToken: String, refreshToken: String): JwtDto
    fun generateNewTokens(id: Long, roles: List<String>): JwtDto
    fun logoutToken(accessToken: String): ResponseCookie
    fun createRefreshToken(refreshToken: String): ResponseCookie
}