package dev.notypie.common.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.notypie.jpa.dao.JpaRegisteredClientRepository
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module
import org.springframework.stereotype.Component

@Component
class MapUtils(
    private val objectMapper: ObjectMapper
) {

    init{
        val classLoader = JpaRegisteredClientRepository::class.java.classLoader
        val securityModules = SecurityJackson2Modules.getModules(classLoader)
        this.objectMapper.registerModules(securityModules)
        this.objectMapper.registerModule(OAuth2AuthorizationServerJackson2Module())
    }

    companion object{
        @JvmStatic fun resolveAuthorizationGrantType(authorizationGrantType: String): AuthorizationGrantType =
            when(authorizationGrantType) {
                AuthorizationGrantType.AUTHORIZATION_CODE.value -> AuthorizationGrantType.AUTHORIZATION_CODE
                AuthorizationGrantType.CLIENT_CREDENTIALS.value -> AuthorizationGrantType.CLIENT_CREDENTIALS
                AuthorizationGrantType.REFRESH_TOKEN.value -> AuthorizationGrantType.REFRESH_TOKEN
                else -> AuthorizationGrantType(authorizationGrantType)
            }

        @JvmStatic fun resolveClientAuthenticationMethod(clientAuthenticationMethod: String): ClientAuthenticationMethod =
            when(clientAuthenticationMethod) {
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC.value -> ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                ClientAuthenticationMethod.CLIENT_SECRET_POST.value -> ClientAuthenticationMethod.CLIENT_SECRET_POST
                ClientAuthenticationMethod.NONE.value -> ClientAuthenticationMethod.NONE
                else -> ClientAuthenticationMethod(clientAuthenticationMethod)
            }
    }
    fun parseMap(data: String): Map<String, Any> {
        try {
            val typeRef: TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}
            return this.objectMapper.readValue(data, typeRef)
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex.message, ex)
        }
    }

    fun writeMap(data: Map<String, Any>): String {
        try {
            return this.objectMapper.writeValueAsString(data)
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex.message, ex)
        }
    }
}