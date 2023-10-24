package dev.notypie.jwt.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank


class LoginRequestDto (
    @JsonProperty("userId")
    private val userId: @NotBlank String,

    @JsonProperty("password")
    private val password: @NotBlank String
){

}