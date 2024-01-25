package dev.notypie.application

import dev.notypie.domain.AuthorizationConsent
import dev.notypie.jpa.dao.AuthorizationConsentRepository
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils


@Service
class JpaOAuth2AuthorizationConsentService(
    private val authorizationConsentRepository: AuthorizationConsentRepository,
    private val registeredClientRepository: RegisteredClientRepository
): OAuth2AuthorizationConsentService{

    override fun save(authorizationConsent: OAuth2AuthorizationConsent?) {
        if(authorizationConsent == null) throw IllegalArgumentException("authorizationConsent cannot be null")
        this.authorizationConsentRepository.save(this.toEntity(authorizationConsent))
    }

    override fun remove(authorizationConsent: OAuth2AuthorizationConsent?) {
        if(authorizationConsent == null) throw IllegalArgumentException("authorizationConsent cannot be null")
        this.authorizationConsentRepository.deleteByRegisteredClientIdAndPrincipalName(
            registeredClientId = authorizationConsent.registeredClientId, principalName = authorizationConsent.principalName
        )
    }

    override fun findById(registeredClientId: String, principalName: String): OAuth2AuthorizationConsent? {
        if(registeredClientId.isBlank()) throw IllegalArgumentException("registeredClientId cannot be empty")
        else if(principalName.isBlank()) throw IllegalArgumentException("principalName cannot be empty")
        val authorizationConsent = this.authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(
            registeredClientId = registeredClientId, principalName = principalName
        )?: return null
        return authorizationConsent.let { this.toObject(it) }
    }

    private fun toObject(authorizationConsent: AuthorizationConsent): OAuth2AuthorizationConsent {
        val registeredClient: RegisteredClient = this.registeredClientRepository.findById(authorizationConsent.registeredClientId) ?:
            throw DataRetrievalFailureException("The RegisteredClient with id '"
                    + authorizationConsent.registeredClientId +
                    "' was not found in the RegisteredClientRepository.")
        return OAuth2AuthorizationConsent.withId(authorizationConsent.registeredClientId, authorizationConsent.principalName)
            .authorities {
                it.addAll(
                    StringUtils.commaDelimitedListToSet(authorizationConsent.authorities)
                        .map{ authority: String -> SimpleGrantedAuthority(authority) }
                )
            }.build()
    }

    private fun toEntity(oAuth2AuthorizationConsent: OAuth2AuthorizationConsent): AuthorizationConsent =
        AuthorizationConsent(
            registeredClientId = oAuth2AuthorizationConsent.registeredClientId,
            principalName = oAuth2AuthorizationConsent.principalName,
            authorities = StringUtils.collectionToCommaDelimitedString(oAuth2AuthorizationConsent.authorities.toSet())
        )
}