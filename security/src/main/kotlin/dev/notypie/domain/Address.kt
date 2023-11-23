package dev.notypie.domain

import com.fasterxml.jackson.annotation.JsonProperty
import dev.notypie.jwt.utils.logger
import jakarta.persistence.Embeddable

@Embeddable
class Address(
    @field:JsonProperty("country")
    private var country: String? = null,

    @field:JsonProperty("street_address")
    private var streetAddress: String? = null,

    @field:JsonProperty("city")
    private var city: String? = null,

    @field:JsonProperty("region")
    private var region: String? = null,

    @field:JsonProperty("zip_code")
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