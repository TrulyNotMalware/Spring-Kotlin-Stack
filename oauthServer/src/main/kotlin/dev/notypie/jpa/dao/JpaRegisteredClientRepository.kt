package dev.notypie.jpa.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module
import org.springframework.stereotype.Service

@Service
class JpaRegisteredClientRepository(
    private val clientRepository: ClientRepository,
    private val objectMapper: ObjectMapper

) : RegisteredClientRepository{

    init{
        val classLoader = JpaRegisteredClientRepository::class.java.classLoader
        val securityModules = SecurityJackson2Modules.getModules(classLoader)
        this.objectMapper.registerModules(securityModules)
        this.objectMapper.registerModule(OAuth2AuthorizationServerJackson2Module())
    }

    companion object{
        @JvmStatic fun resolveAuthorizationGrantType(authorizationGrantType: String): AuthorizationGrantType {
            if (AuthorizationGrantType.AUTHORIZATION_CODE.value == authorizationGrantType) {
                return AuthorizationGrantType.AUTHORIZATION_CODE
            } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.value == authorizationGrantType) {
                return AuthorizationGrantType.CLIENT_CREDENTIALS
            } else if (AuthorizationGrantType.REFRESH_TOKEN.value == authorizationGrantType) {
                return AuthorizationGrantType.REFRESH_TOKEN
            }
            return AuthorizationGrantType(authorizationGrantType) // Custom authorization grant type
        }

        @JvmStatic fun resolveClientAuthenticationMethod(clientAuthenticationMethod: String): ClientAuthenticationMethod{
            if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.value == clientAuthenticationMethod) {
                return ClientAuthenticationMethod.CLIENT_SECRET_BASIC
            } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.value == clientAuthenticationMethod) {
                return ClientAuthenticationMethod.CLIENT_SECRET_POST
            } else if (ClientAuthenticationMethod.NONE.value == clientAuthenticationMethod) {
                return ClientAuthenticationMethod.NONE
            }
            return ClientAuthenticationMethod(clientAuthenticationMethod) // Custom client authentication method
        }
    }
    override fun save(registeredClient: RegisteredClient) {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): RegisteredClient {
        TODO("Not yet implemented")
    }

    override fun findByClientId(clientId: String): RegisteredClient {
        TODO("Not yet implemented")
    }

//    private fun toEntity(registeredClient: RegisteredClient): Client {
//        val clientAuthenticationMethods: MutableList<String> = mutableListOf()
//        registeredClient.clientAuthenticationMethods.forEach{ clientAuthenticationMethods.add(it.value) }
//        val authorizationGrantType: MutableList<String> = mutableListOf()
//        registeredClient.authorizationGrantTypes.forEach { authorizationGrantType.add(it.value) }
//        return Client(id = registeredClient.id.toLong(),
//            clientId = registeredClient.clientId, clientIdIssuedAt = registeredClient.clientIdIssuedAt,
//            clientSecret = registeredClient.clientSecret)
//    }

    private fun parseMap(data: String): Map<String, Any> {
        try {
            val typeRef: TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}
            return this.objectMapper.readValue(data, typeRef)
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex.message, ex)
        }
    }

    private fun writeMap(data: Map<String, Any>): String {
        try {
            return this.objectMapper.writeValueAsString(data)
        } catch (ex: Exception) {
            throw IllegalArgumentException(ex.message, ex)
        }
    }

}