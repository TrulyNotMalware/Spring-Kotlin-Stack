package dev.notypie.application

import com.fasterxml.jackson.databind.ObjectMapper
import dev.notypie.jpa.dao.AuthorizationRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

class JpaOAuth2AuthorizationService(
    private val authorizationRepository: AuthorizationRepository,
    private val registeredClientRepository: RegisteredClientRepository,
    private val objectMapper: ObjectMapper
){
}