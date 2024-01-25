package dev.notypie.application

import dev.notypie.common.utils.MapUtils
import dev.notypie.domain.Authorization
import dev.notypie.jpa.dao.AuthorizationRepository
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2DeviceCode
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.OAuth2UserCode
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class JpaOAuth2AuthorizationService(
    private val authorizationRepository: AuthorizationRepository,
    private val registeredClientRepository: RegisteredClientRepository,
    private val mapUtils: MapUtils
): OAuth2AuthorizationService{
    override fun save(authorization: OAuth2Authorization?) {
        if( authorization == null ) throw IllegalArgumentException("authorization cannot be null")
        this.authorizationRepository.save(this.toEntity(authorization = authorization))
    }

    override fun remove(authorization: OAuth2Authorization?) {
        if( authorization == null ) throw IllegalArgumentException("authorization cannot be null")
        this.authorizationRepository.deleteById(authorization.id)
    }

    override fun findById(id: String?): OAuth2Authorization? {
        if( id.isNullOrBlank() ) throw IllegalArgumentException("id cannot be empty")
        return this.authorizationRepository.findByIdOrNull(id)?.let { this.toObject(it) }
    }

    override fun findByToken(token: String?, tokenType: OAuth2TokenType?): OAuth2Authorization? {
        if( token.isNullOrBlank() ) throw IllegalArgumentException("token cannot be empty")
        if ( tokenType == null )
            return this.authorizationRepository.findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(token = token)
                ?.let { this.toObject(it) }
        return when(tokenType.value){
            OAuth2ParameterNames.STATE -> this.authorizationRepository.findByState(state = token)?.let { this.toObject(it) }
            OAuth2ParameterNames.CODE -> this.authorizationRepository.findByAuthorizationCodeValue(authorizationCode = token)?.let { this.toObject(it) }
            OAuth2ParameterNames.ACCESS_TOKEN -> this.authorizationRepository.findByAccessTokenValue(accessToken = token)?.let { this.toObject(it) }
            OAuth2ParameterNames.REFRESH_TOKEN -> this.authorizationRepository.findByRefreshTokenValue(refreshToken = token)?.let { this.toObject(it) }
            OidcParameterNames.ID_TOKEN -> this.authorizationRepository.findByOidcIdTokenValue(idToken = token)?.let { this.toObject(it) }
            OAuth2ParameterNames.USER_CODE -> this.authorizationRepository.findByUserCodeValue(userCode = token)?.let { this.toObject(it) }
            OAuth2ParameterNames.DEVICE_CODE -> this.authorizationRepository.findByDeviceCodeValue(deviceCode = token)?.let { this.toObject(it) }
            else -> null
        }
    }

    private fun toObject(authorization: Authorization): OAuth2Authorization{
        val registeredClient: RegisteredClient = this.registeredClientRepository.findById(authorization.registeredClientId) ?:
            throw DataRetrievalFailureException("The RegisteredClient with id '"
                    + authorization.registeredClientId
                    + "' was not found in the RegisteredClientRepository.")
        val builder = OAuth2Authorization.withRegisteredClient(registeredClient)
            .id(authorization.id).principalName(authorization.principalName)
            .authorizationGrantType(MapUtils.resolveAuthorizationGrantType(authorization.authorizationGrantType))
            .authorizedScopes(StringUtils.commaDelimitedListToSet(authorization.authorizedScopes))
            .attributes { it.putAll(mapUtils.parseMap(authorization.attributes)) }
        if (authorization.state != null) {
            builder.attribute(OAuth2ParameterNames.STATE, authorization.state)
        }
        if (authorization.authorizationCodeValue != null && authorization.authorizationCodeMetadata != null)
            builder.token(OAuth2AuthorizationCode(
                authorization.authorizationCodeValue,
                authorization.authorizationCodeIssuedAt,
                authorization.authorizationCodeExpiresAt
            )) { it.putAll(mapUtils.parseMap(authorization.authorizationCodeMetadata)) }

        if(authorization.accessTokenValue != null && authorization.accessTokenMetadata != null)
            builder.token(OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                authorization.accessTokenValue,
                authorization.accessTokenIssuedAt,
                authorization.accessTokenExpiresAt,
                StringUtils.commaDelimitedListToSet(authorization.accessTokenScopes)
            )) { it.putAll(mapUtils.parseMap(authorization.accessTokenMetadata)) }

        if(authorization.refreshTokenValue != null && authorization.refreshTokenMetadata != null)
            builder.token(OAuth2RefreshToken(
                authorization.refreshTokenValue,
                authorization.refreshTokenIssuedAt,
                authorization.refreshTokenExpiresAt
            )) { it.putAll(mapUtils.parseMap(authorization.refreshTokenMetadata)) }

        if(authorization.refreshTokenValue != null && authorization.refreshTokenMetadata != null)
            builder.token(OAuth2RefreshToken(
                authorization.refreshTokenValue,
                authorization.refreshTokenIssuedAt,
                authorization.refreshTokenExpiresAt
            )) { it.putAll(mapUtils.parseMap(authorization.refreshTokenMetadata)) }

        if(authorization.oidcIdTokenValue != null && authorization.oidcIdTokenMetadata != null)
            builder.token(OidcIdToken(
                authorization.oidcIdTokenValue,
                authorization.oidcIdTokenIssuedAt,
                authorization.oidcIdTokenExpiresAt,
                authorization.oidcIdTokenClaims?.let { mapUtils.parseMap(it) }
            )) { it.putAll(mapUtils.parseMap(authorization.oidcIdTokenMetadata)) }

        if(authorization.userCodeValue != null && authorization.userCodeMetadata != null)
            builder.token(OAuth2UserCode(
                authorization.userCodeValue,
                authorization.userCodeIssuedAt,
                authorization.userCodeExpiresAt
            )) { it.putAll(mapUtils.parseMap(authorization.userCodeMetadata)) }

        if(authorization.deviceCodeValue != null && authorization.deviceCodeMetadata != null)
            builder.token(OAuth2DeviceCode(
                authorization.deviceCodeValue,
                authorization.deviceCodeIssuedAt,
                authorization.deviceCodeExpiresAt
            )) { it.putAll(mapUtils.parseMap(authorization.deviceCodeMetadata)) }
        return builder.build()
    }

    private fun toEntity(authorization: OAuth2Authorization): Authorization {

        val authorizationCode = authorization.getToken(OAuth2AuthorizationCode::class.java)
        val accessToken = authorization.getToken(OAuth2AccessToken::class.java)
        val refreshToken = authorization.getToken(OAuth2RefreshToken::class.java);
        val oidcIdToken = authorization.getToken(OidcIdToken::class.java)
        val userCode = authorization.getToken(OAuth2UserCode::class.java)
        val deviceCode = authorization.getToken(OAuth2DeviceCode::class.java)

        return Authorization(
            id = authorization.id, registeredClientId = authorization.registeredClientId,
            principalName = authorization.principalName,
            authorizationGrantType = authorization.authorizationGrantType.value,
            authorizedScopes = StringUtils.collectionToDelimitedString(authorization.authorizedScopes, ","),
            attributes = mapUtils.writeMap(authorization.attributes),
            state = authorization.getAttribute(OAuth2ParameterNames.STATE),

            authorizationCodeValue = authorizationCode?.token?.tokenValue,
            authorizationCodeIssuedAt = authorizationCode?.token?.issuedAt,
            authorizationCodeExpiresAt = authorizationCode?.token?.expiresAt,
            authorizationCodeMetadata = authorizationCode?.metadata?.let { mapUtils.writeMap(it) },

            accessTokenValue = accessToken?.token?.tokenValue,
            accessTokenIssuedAt = accessToken?.token?.issuedAt,
            accessTokenExpiresAt = accessToken?.token?.expiresAt,
            accessTokenMetadata = accessToken?.metadata?.let { mapUtils.writeMap(it) },

            accessTokenScopes = accessToken?.token?.scopes?.let { StringUtils.collectionToDelimitedString(it, ",") },
            refreshTokenValue = refreshToken?.token?.tokenValue,
            refreshTokenIssuedAt = refreshToken?.token?.issuedAt,
            refreshTokenExpiresAt = refreshToken?.token?.expiresAt,
            refreshTokenMetadata = refreshToken?.metadata?.let { mapUtils.writeMap(it) },

            oidcIdTokenValue = oidcIdToken?.token?.tokenValue,
            oidcIdTokenIssuedAt = oidcIdToken?.token?.issuedAt,
            oidcIdTokenExpiresAt = oidcIdToken?.token?.expiresAt,
            oidcIdTokenMetadata = oidcIdToken?.metadata?.let { mapUtils.writeMap(it) },
            oidcIdTokenClaims = oidcIdToken?.claims?.let { mapUtils.writeMap(it) },

            userCodeValue = userCode?.token?.tokenValue,
            userCodeIssuedAt = userCode?.token?.issuedAt,
            userCodeExpiresAt = userCode?.token?.expiresAt,
            userCodeMetadata = userCode?.metadata?.let { mapUtils.writeMap(it) },

            deviceCodeValue = deviceCode?.token?.tokenValue,
            deviceCodeIssuedAt = deviceCode?.token?.issuedAt,
            deviceCodeExpiresAt = deviceCode?.token?.expiresAt,
            deviceCodeMetadata = deviceCode?.metadata?.let { mapUtils.writeMap(it) }
        )
    }

}