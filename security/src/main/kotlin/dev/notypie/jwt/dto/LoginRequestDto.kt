package dev.notypie.jwt.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank


class LoginRequestDto (
    @JsonProperty("userId")
    val userId: @NotBlank String,

    @JsonProperty("password")
    val password: @NotBlank String
){

}