package dev.notypie.application

import dev.notypie.dto.RegisterOAuthClient
import dev.notypie.dto.ResponseRegisteredClient

interface OAuth2Service {

    fun registerNewClient(oAuthClient: RegisterOAuthClient): ResponseRegisteredClient

}