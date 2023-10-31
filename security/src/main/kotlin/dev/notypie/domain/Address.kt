package dev.notypie.domain

import com.fasterxml.jackson.annotation.JsonProperty
import dev.notypie.jwt.utils.logger
import jakarta.persistence.Embeddable

@Embeddable
class Address(
    @JsonProperty("country")
    private val country: String?,

    @JsonProperty("street_address")
    private val streetAddress: String?,

    @JsonProperty("city")
    private val city: String?,

    @JsonProperty("region")
    private val region: String?,

    @JsonProperty("zip_code")
    private val zipCode: String?
) {
    companion object{
        private val log = logger()
    }
}