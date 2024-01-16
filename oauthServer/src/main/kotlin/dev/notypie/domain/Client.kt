package dev.notypie.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.uuid.Generators
import dev.notypie.dto.RegisterOAuthClient
import dev.notypie.dto.ResponseRegisteredClient
import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.util.StringUtils
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Entity(name = "client")
@SequenceGenerator(
    name = "CLIENT_SQ_GENERATOR",
    sequenceName = "CLIENT_SEQ",
    initialValue = 1,
    allocationSize = 1
)
class Client(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENT_SEQ")
    val id: Long = 0L,
    val clientId: String = this.generateClientId(),
    val clientIdIssuedAt: Instant = Instant.now(),

    @field:Transient
    val rawPassword: String = this.generatePassword(),

    val clientSecret: String = BCryptPasswordEncoder().encode(rawPassword),
    val clientSecretExpiresAt: Instant = Instant.now().plus(24, ChronoUnit.HOURS),
    val clientName: String,
    @field:Column(length = 1000)
    val clientAuthenticationMethods: String = ClientAuthenticationMethod.CLIENT_SECRET_BASIC.value,
    @field:Column(length = 1000)
    val authorizationGrantTypes: String = AuthorizationGrantType.AUTHORIZATION_CODE.value,
    @field:Column(length = 1000)
    val redirectUris: String,
    @field:Column(length = 1000)
    val postLogoutRedirectUris: String,
    @field:Column(length = 1000)
    val scopes: String,
    @field:Column(length = 2000)
    val clientSettings: String = this.writeMap(ClientSettings.builder().requireAuthorizationConsent(true).build().settings),
    @field:Column(length = 2000)
    val tokenSettings: String = this.writeMap(TokenSettings.builder().build().settings),
) {
    companion object{
        @JvmStatic fun createDefaultClient(blueprint: RegisterOAuthClient):Client =
            Client(clientName = blueprint.clientName, redirectUris = blueprint.redirectUris,
                scopes=StringUtils.collectionToCommaDelimitedString(blueprint.scopes),
                postLogoutRedirectUris = blueprint.postLogoutRedirectUris)

        private fun generateClientId(): String
                = Generators.timeBasedGenerator().generate().toString().replace("-","")

        private fun generatePassword():String{
            val leftLimit = 48 // numeral '0'
            val rightLimit = 122 // letter 'z'
            val targetStringLength = 30L
            val random:Random = Random()
            return random.ints(leftLimit, rightLimit+1)
                .filter { (it <= 57 || it>= 65) && (it <=90 || it >=97) }
                .limit(targetStringLength)
                .collect(
                    { StringBuilder() },
                    StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
        }

        private fun writeMap(data: Map<String, Any>): String{
            try{
                return ObjectMapper().registerModules(JavaTimeModule()).writeValueAsString(data)
            } catch( e:Exception ){
                throw IllegalArgumentException(e.message, e)
            }
        }
    }

    fun isPublicClientType(): Boolean{
        val authenticationMethods: Set<String> = StringUtils.commaDelimitedListToSet(this.clientAuthenticationMethods)
        return StringUtils.commaDelimitedListToSet(this.authorizationGrantTypes)
            .contains(AuthorizationGrantType.AUTHORIZATION_CODE.value) &&
                authenticationMethods.size == 1 &&
                authenticationMethods.contains(ClientAuthenticationMethod.NONE.value)
    }

    fun toResponseDto(): ResponseRegisteredClient =
        ResponseRegisteredClient(
            clientId=clientId, clientSecret=rawPassword,
            clientSecretExpiresAt=clientSecretExpiresAt, clientName=clientName)
}