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
    @JsonProperty("refreshToken")
    @Column(name = "refresh_token", length = 500)
    private var refreshToken: String?,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonProperty("refresh_authenticated_at")
    private var refreshAuthenticatedAt: LocalDateTime?,
){

    fun update(refreshToken: String){
        this.refreshToken = refreshToken
        this.refreshAuthenticatedAt = LocalDateTime.now()
    }

}