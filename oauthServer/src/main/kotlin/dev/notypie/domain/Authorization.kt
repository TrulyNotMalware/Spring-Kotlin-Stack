package dev.notypie.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant


@Entity(name = "authorization")
class Authorization(
    @field:Id
    @field:Column
    val id: String,
    val registeredClientId: String,
    val principalName: String,
    val authorizationGrantType: String,
    @field:Column(length = 1000)
    val authorizedScopes: String,

    @field:Column(length = 4000)
    val attributes: String,

    @field:Column(length = 500)
    val state: String?,

    @field:Column(length = 4000)
    val authorizationCodeValue: String?,
    val authorizationCodeIssuedAt: Instant?,
    val authorizationCodeExpiresAt: Instant?,
    val authorizationCodeMetadata: String?,

    @field:Column(length = 4000)
    val accessTokenValue: String?,
    val accessTokenIssuedAt: Instant?,
    val accessTokenExpiresAt: Instant?,

    @field:Column(length = 2000)
    val accessTokenMetadata: String?,
    val accessTokenType: String? = null,

    @field:Column(length = 1000)
    val accessTokenScopes: String?,

    @field:Column(length = 4000)
    val refreshTokenValue: String?,
    val refreshTokenIssuedAt: Instant?,
    val refreshTokenExpiresAt: Instant?,

    @field:Column(length = 2000)
    val refreshTokenMetadata: String?,

    @field:Column(length = 4000)
    val oidcIdTokenValue: String?,
    val oidcIdTokenIssuedAt: Instant?,
    val oidcIdTokenExpiresAt: Instant?,

    @field:Column(length = 2000)
    val oidcIdTokenMetadata: String?,

    @field:Column(length = 2000)
    val oidcIdTokenClaims: String?,

    @field:Column(length = 4000)
    val userCodeValue: String?,
    val userCodeIssuedAt: Instant?,
    val userCodeExpiresAt: Instant?,

    @field:Column(length = 2000)
    val userCodeMetadata: String?,

    @field:Column(length = 4000)
    val deviceCodeValue: String?,
    val deviceCodeIssuedAt: Instant?,
    val deviceCodeExpiresAt: Instant?,

    @field:Column(length = 2000)
    val deviceCodeMetadata: String?
)