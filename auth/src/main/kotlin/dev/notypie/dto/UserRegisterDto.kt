package dev.notypie.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class UserRegisterDto(
    @field:JsonProperty("userId")
    @field:NotBlank(message = "User name must require validate value.")
    @field:Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    val userId: String,

    @field:JsonProperty("userName")
    @field:NotBlank(message = "User name must require validate value.")
    @field:Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    val userName: String,

    @field:JsonProperty("email")
    @field:NotBlank(message = "valid email address must required.")
    @field:Email
    val email: String,

    @field:JsonProperty("password")
    @field:Pattern(regexp = "^[a-zA-Z0-9_#@!-]*$")
    val password: String,

    @field:JsonProperty("phoneNumber")
    @field:NotBlank
    @field:Pattern(
    regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
    message = "valid phone number must required.")
    val phoneNumber: String,

    // Nullable.
    @field:JsonProperty("country")
    val country: String? = null,

    @field:JsonProperty("streetAddress")
    val streetAddress: String? = null,

    @field:JsonProperty("city")
    val city: String? = null,

    @field:JsonProperty("region")
    val region: String? = null,

    @field:JsonProperty("zipCode")
    val zipCode: String? = null,
)