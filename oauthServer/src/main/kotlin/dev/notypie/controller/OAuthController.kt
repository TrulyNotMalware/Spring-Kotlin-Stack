package dev.notypie.controller

import dev.notypie.application.OAuth2Service
import dev.notypie.dto.RegisterOAuthClient
import dev.notypie.dto.ResponseRegisteredClient
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.MediaTypes
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/oauth2")
class OAuthController(
    private val service: OAuth2Service
) {

    @PostMapping(value = ["/client"], produces = [MediaTypes.HAL_JSON_VALUE])
    fun register(@RequestBody oAuthClient: RegisterOAuthClient):
            EntityModel<ResponseRegisteredClient>{
        return EntityModel.of(this.service.registerNewClient(oAuthClient = oAuthClient))
    }
}