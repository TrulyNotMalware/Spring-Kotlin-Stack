package dev.notypie.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class UserRegisterDto(
    @JsonProperty("userId")
    @field:NotBlank(message = "User name must require validate value.")
    @field:Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    val userId: String,

    @JsonProperty("userName")
    @field:NotBlank(message = "User name must require validate value.")
    @field:Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    val userName: String,

    @JsonProperty("email")
    @field:NotBlank(message = "valid email address must required.")
    @field:Email
    val email: String,

    @JsonProperty("password")
    @field:Pattern(regexp = "^[a-zA-Z0-9_#@!-]*$")
    val password: String,

    @JsonProperty("phoneNumber")
    @NotBlank @Pattern(
    regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$",
    message = "valid phone number must required.")
    val phoneNumber: String,

    // Nullable.
    @JsonProperty("country")
    val country: String? = null,

    @JsonProperty("streetAddress")
    val streetAddress: String? = null,

    @JsonProperty("city")
    val city: String? = null,

    @JsonProperty("region")
    val region: String? = null,

    @JsonProperty("zipCode")
    val zipCode: String? = null,
)