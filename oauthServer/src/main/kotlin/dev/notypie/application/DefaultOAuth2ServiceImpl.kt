package dev.notypie.application

import dev.notypie.dto.RegisterOAuthClient
import dev.notypie.dto.ResponseRegisteredClient
import org.springframework.stereotype.Service

@Service
class DefaultOAuth2ServiceImpl(
//    private val clientRepository: ClientRepository
) : OAuth2Service{


    override fun registerNewClient(oAuthClient: RegisterOAuthClient): ResponseRegisteredClient {
        TODO("Not yet implemented")
    }

}