package dev.notypie.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime


@Embeddable
class RefreshToken (
    @field:JsonProperty("refreshToken")
    @Column(name = "refresh_token", length = 500)
    private var refreshToken: String? = null,

    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @Column(name = "refresh_authenticated_at")
    private var refreshAuthenticatedAt: LocalDateTime? = null,
){

    internal fun update(refreshToken: String?){
        this.refreshToken = refreshToken
        this.refreshAuthenticatedAt = LocalDateTime.now()
    }

    internal fun getRefreshToken() = this.refreshToken
}