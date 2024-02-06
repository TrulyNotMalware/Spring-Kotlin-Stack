package dev.notypie.configurations

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator
import org.springframework.security.web.SecurityFilterChain
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun configure(): WebSecurityCustomizer =
        WebSecurityCustomizer {
            it.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/h2-console/**",
                "/error"
            )
        }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings = AuthorizationServerSettings.builder().build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager = configuration.authenticationManager

    @Bean
    fun jwtEncoder(contextJWKSource: JWKSource<SecurityContext>) = NimbusJwtEncoder(contextJWKSource)

    @Bean
    fun securityContextJWKSource(privateKey: RSAPrivateKey, publicKey: RSAPublicKey): JWKSource<SecurityContext>
    = ImmutableJWKSet(
        JWKSet(
            RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build()
        )
    )

    @Bean
    @Profile("jpa-oauth-server")
    fun filterChain(
        httpSecurity: HttpSecurity,
        registeredClientRepository: RegisteredClientRepository,
        authorizationService: OAuth2AuthorizationService,
        authorizationConsentService: OAuth2AuthorizationConsentService,
        jwtEncoder: JwtEncoder,
        settings: AuthorizationServerSettings
        ): SecurityFilterChain{
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer()
            .oidc(Customizer.withDefaults())
            .registeredClientRepository(registeredClientRepository)
            .authorizationService(authorizationService)
            .tokenGenerator(JwtGenerator(jwtEncoder))
            .authorizationServerSettings(settings)

        val endpointsMatcher = authorizationServerConfigurer.endpointsMatcher

        httpSecurity.authorizeHttpRequests { it.anyRequest().authenticated() }
            .csrf { it.ignoringRequestMatchers(endpointsMatcher) }
            .with(authorizationServerConfigurer, Customizer.withDefaults())

        httpSecurity.formLogin(Customizer.withDefaults())

        return httpSecurity.build()
    }
}
