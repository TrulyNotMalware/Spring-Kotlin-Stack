package dev.notypie.domain

import com.fasterxml.jackson.annotation.JsonProperty
import dev.notypie.jwt.utils.logger
import jakarta.persistence.Embeddable

@Embeddable
class Address(
    @JsonProperty("country")
    private var country: String? = null,

    @JsonProperty("street_address")
    private var streetAddress: String? = null,

    @JsonProperty("city")
    private var city: String? = null,

    @JsonProperty("region")
    private var region: String? = null,

    @JsonProperty("zip_code")
    private var zipCode: String? = null
) {
    companion object{
        private val log = logger()
    }

    internal fun update(address: Address){
        this.country = address.country
        this.streetAddress = address.streetAddress
        this.city = address.city
        this.region = address.region
        this.zipCode = address.zipCode
    }
}