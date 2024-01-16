package dev.notypie.application

import dev.notypie.domain.Client
import dev.notypie.dto.RegisterOAuthClient
import dev.notypie.dto.ResponseRegisteredClient
import dev.notypie.jpa.dao.ClientRepository
import dev.notypie.jwt.utils.logger
import org.springframework.stereotype.Service

@Service
class DefaultOAuth2ServiceImpl(
    private val clientRepository: ClientRepository
) : OAuth2Service{

    private val log = logger()

    override fun registerNewClient(oAuthClient: RegisterOAuthClient): ResponseRegisteredClient
    = this.clientRepository.save(Client.createDefaultClient(oAuthClient)).toResponseDto()


}