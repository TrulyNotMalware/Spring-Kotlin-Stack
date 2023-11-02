package dev.notypie.application

import dev.notypie.common.utils.OAuthAttribute
import dev.notypie.dao.UsersRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val repository: UsersRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User>{

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val user = delegate.loadUser(userRequest)
        //OAuth Service Name.
        val registrationId = userRequest.clientRegistration.registrationId
        //OAuth Login key
        val userNameAttributeName = userRequest.clientRegistration.providerDetails
            .userInfoEndpoint.userNameAttributeName
        this.repository.saveOrUpdateByUserId(
            OAuthAttribute.extract(registrationId, user.attributes)
        )
        return DefaultOAuth2User(
            setOf(SimpleGrantedAuthority("oauth-user")),
            user.attributes, userNameAttributeName
        )
    }
}