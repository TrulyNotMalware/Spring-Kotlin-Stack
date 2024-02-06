package dev.notypie.common.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.notypie.base.annotations.JpaDaoTest
import dev.notypie.domain.Client
import dev.notypie.dto.RegisterOAuthClient
import dev.notypie.jpa.dao.ClientRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.StringUtils

@ActiveProfiles("jpa-oauth-server")
@JpaDaoTest
class TokenSettingSerializerTest @Autowired constructor(
    private val clientRepository: ClientRepository
): BehaviorSpec({
    val scopes = mutableSetOf("test.read","test.write")
    val blueprint: RegisterOAuthClient = RegisterOAuthClient(
        clientName = "testClient", redirectUris = "http://localhost/login/oauth2/code/test",
        postLogoutRedirectUris = "http://localhost/logout", scopes = scopes
    )

    given("client"){

        val client: Client = Client(clientName = blueprint.clientName,
            redirectUris = blueprint.redirectUris,
            postLogoutRedirectUris = blueprint.postLogoutRedirectUris,
            scopes = StringUtils.collectionToCommaDelimitedString(scopes)
        )
        val original: TokenSettings = TokenSettings.builder().build()

        `when`("save and find"){
            clientRepository.save(client)
            val getClient: Client = clientRepository.findByClientId(clientId = client.clientId) ?:
            throw AssertionError("client should not be null")

            val typeRef: TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}
            val parsedMap: Map<String, Any> = ObjectMapper().readValue(getClient.tokenSettings, typeRef)
            parsedMap.size shouldNotBe 0
            val tokenSerializer = TokenSettingsSerializer(parsedMap)
            val deserializedTokenSettings = tokenSerializer.tokenSettings

            then("successfully deserialized"){
                // 1. OAUthTokenFormat
                deserializedTokenSettings.accessTokenFormat shouldBeEqual original.accessTokenFormat
                // 2. Duration value
                deserializedTokenSettings.accessTokenTimeToLive shouldBeEqual original.accessTokenTimeToLive
                deserializedTokenSettings.authorizationCodeTimeToLive shouldBeEqual original.authorizationCodeTimeToLive
                deserializedTokenSettings.deviceCodeTimeToLive shouldBeEqual original.deviceCodeTimeToLive
                deserializedTokenSettings.refreshTokenTimeToLive shouldBeEqual original.refreshTokenTimeToLive
                // Signature Algorithm
                deserializedTokenSettings.idTokenSignatureAlgorithm shouldBeEqual original.idTokenSignatureAlgorithm
            }
        }
    }

})