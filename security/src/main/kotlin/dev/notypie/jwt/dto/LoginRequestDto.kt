package dev.notypie.jwt.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank


class LoginRequestDto (
    @field:JsonProperty("userId")
    @field:NotBlank
    val userId: String,

    @field:JsonProperty("password")
    @field:NotBlank
    val password: String
){

}