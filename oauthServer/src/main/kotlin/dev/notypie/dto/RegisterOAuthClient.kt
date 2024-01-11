package dev.notypie.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

class RegisterOAuthClient(
    @field:JsonProperty("clientName")
    @field:NotBlank
    val clientName: String,

    @field:JsonProperty("redirectUris")
    @field:NotBlank
    val redirectUris: String,

    @field:JsonProperty("postLogoutRedirectUris")
    val postLogoutRedirectUris: String,

    @field:JsonProperty("redirectUris")
    @field:NotBlank
    val scopes: Set<String>
)