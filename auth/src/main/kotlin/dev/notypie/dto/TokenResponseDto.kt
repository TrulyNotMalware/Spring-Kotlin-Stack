package dev.notypie.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.notypie.jwt.dto.JwtDto
import java.text.SimpleDateFormat

class TokenResponseDto(
    @JsonProperty("accessToken") private val accessToken: String,
    @JsonProperty("expired") private val expired: String
) {
    companion object {
        fun toTokenResponseDto(jwtDto: JwtDto): TokenResponseDto =
            TokenResponseDto(jwtDto.accessToken, SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(jwtDto.accessTokenExpiredDate))
    }
}