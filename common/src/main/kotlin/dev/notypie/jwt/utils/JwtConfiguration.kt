package dev.notypie.jwt.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("jwt", "oauth")
class JwtConfiguration {

    @Value("\${jwt.token.keystore.classpath}")
    lateinit var keyStorePath: String
}