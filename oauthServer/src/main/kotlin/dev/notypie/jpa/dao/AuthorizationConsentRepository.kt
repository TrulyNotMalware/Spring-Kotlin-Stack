package dev.notypie.jpa.dao

import dev.notypie.domain.AuthorizationConsent
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Profile("jpa-oauth-server")
interface AuthorizationConsentRepository : JpaRepository<AuthorizationConsent, AuthorizationConsent.AuthorizationConsentId>{

    fun findByRegisteredClientIdAndPrincipalName(registeredClientId: String, principalName: String): AuthorizationConsent?
    fun deleteByRegisteredClientIdAndPrincipalName(registeredClientId: String, principalName: String)
}