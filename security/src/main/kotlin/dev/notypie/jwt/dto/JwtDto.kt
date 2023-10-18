package dev.notypie.jwt.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JwtDto(
    @JsonProperty("access_token") private val accessToken: String,
    @JsonProperty("refresh_token") private val refreshToken: String
) {
}