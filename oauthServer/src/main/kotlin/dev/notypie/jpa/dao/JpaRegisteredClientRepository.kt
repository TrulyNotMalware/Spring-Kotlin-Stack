package dev.notypie.jpa.dao

import dev.notypie.common.utils.MapUtils
import dev.notypie.common.utils.TokenSettingsSerializer
import dev.notypie.domain.Client
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class JpaRegisteredClientRepository(
    private val clientRepository: ClientRepository,
    private val mapUtils: MapUtils
) : RegisteredClientRepository{

    override fun save(registeredClient: RegisteredClient) {
        this.clientRepository.save(this.toEntity(registeredClient))
    }

    override fun findById(id: String): RegisteredClient? {
        if(id.isBlank()) throw IllegalArgumentException("id cannot be empty")
        val client: Client = this.clientRepository.findByIdOrNull(id.toLong()) ?: return null
        return this.toObject(client = client)
    }

    override fun findByClientId(clientId: String): RegisteredClient? {
        if(clientId.isBlank()) throw IllegalArgumentException("clientId cannot be empty")
        val client: Client = this.clientRepository.findByClientId(clientId = clientId) ?: return null
        return this.toObject(client = client)
    }

    private fun toObject(client: Client): RegisteredClient =
        RegisteredClient.withId(client.id.toString())
            .clientId(client.clientId)
            .clientIdIssuedAt(client.clientIdIssuedAt)
            .clientSecret(client.clientSecret)
            .clientSecretExpiresAt(client.clientSecretExpiresAt)
            .clientName(client.clientName)
            .clientAuthenticationMethods {
                it.addAll(
                    StringUtils.commaDelimitedListToSet(client.clientAuthenticationMethods)
                        .map { method: String -> MapUtils.resolveClientAuthenticationMethod(method) }
                )
            }
            .authorizationGrantTypes {
                it.addAll(
                    StringUtils.commaDelimitedListToSet(client.authorizationGrantTypes)
                        .map { grantType: String -> MapUtils.resolveAuthorizationGrantType(grantType) }
                )
            }
            .redirectUris{ it.addAll(StringUtils.commaDelimitedListToSet(client.redirectUris)) }
            .postLogoutRedirectUris { it.addAll(StringUtils.commaDelimitedListToSet(client.postLogoutRedirectUris)) }
            .scopes { it.addAll(StringUtils.commaDelimitedListToSet(client.scopes)) }
            .clientSettings(ClientSettings.withSettings(mapUtils.parseMap(client.clientSettings)).build())
            .tokenSettings(TokenSettingsSerializer(mapUtils.parseMap(client.tokenSettings)).tokenSettings)
            .build()

    private fun toEntity(registeredClient: RegisteredClient): Client
        = Client(id = registeredClient.id.toLong(), clientId = registeredClient.clientId,
            clientIdIssuedAt = registeredClient.clientIdIssuedAt!!, clientSecret = registeredClient.clientSecret!!,
            clientSecretExpiresAt = registeredClient.clientSecretExpiresAt!!, clientName = registeredClient.clientName,
            clientAuthenticationMethods = StringUtils.collectionToCommaDelimitedString(registeredClient.clientAuthenticationMethods.map { it.value }),
            authorizationGrantTypes = StringUtils.collectionToCommaDelimitedString(registeredClient.authorizationGrantTypes.map { it.value }),
            redirectUris = StringUtils.collectionToCommaDelimitedString(registeredClient.redirectUris),
            postLogoutRedirectUris = StringUtils.collectionToCommaDelimitedString(registeredClient.postLogoutRedirectUris),
            scopes = StringUtils.collectionToCommaDelimitedString(registeredClient.scopes),
            clientSettings = mapUtils.writeMap(registeredClient.clientSettings.settings),
            tokenSettings = mapUtils.writeMap(registeredClient.tokenSettings.settings)
        )
}