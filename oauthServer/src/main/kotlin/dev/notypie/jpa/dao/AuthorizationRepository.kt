package dev.notypie.jpa.dao

import dev.notypie.domain.Authorization
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Profile("jpa-oauth-server")
interface AuthorizationRepository: JpaRepository<Authorization, String> {

    fun findByState(state: String): Authorization?
    fun findByAuthorizationCodeValue(authorizationCode: String): Authorization?
    fun findByAccessTokenValue(accessToken: String): Authorization?
    fun findByRefreshTokenValue(refreshToken: String): Authorization?
    fun findByOidcIdTokenValue(idToken: String): Authorization?
    fun findByUserCodeValue(userCode: String): Authorization?
    fun findByDeviceCodeValue(deviceCode: String): Authorization?

    @Query(
        "select a from authorization a where a.state = :token" +
                " or a.authorizationCodeValue = :token" +
                " or a.accessTokenValue = :token" +
                " or a.refreshTokenValue = :token" +
                " or a.oidcIdTokenValue = :token" +
                " or a.userCodeValue = :token" +
                " or a.deviceCodeValue = :token"
    )
    fun findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValueOrOidcIdTokenValueOrUserCodeValueOrDeviceCodeValue(
        @Param("token") token: String): Authorization?
}