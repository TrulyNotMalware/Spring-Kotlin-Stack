package dev.notypie.configurations

import dev.notypie.application.LoginAuthenticationFilter
import dev.notypie.jwt.utils.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val environment: Environment,
    @Value("\${authentication.login.requestUrl}") private val loginRequestUrl: String,
    @Value("\${authentication.logout.requestUrl}") private val logoutRequestUrl: String
) {
    private val log = logger()

    @Bean
    fun configure(): WebSecurityCustomizer
        = WebSecurityCustomizer {
            it.ignoring()
                .requestMatchers(AntPathRequestMatcher("/api/auth/**"))
                .requestMatchers(AntPathRequestMatcher("/api/auth/**"))
                .requestMatchers(AntPathRequestMatcher("/h2-console/**"))
        }

    @Bean
    @Profile("jwt")
    fun filterChain(
        httpSecurity: HttpSecurity,
        filter: LoginAuthenticationFilter,
        userService: OAuth2UserService<OAuth2UserRequest, OAuth2User>,
        introspect: HandlerMappingIntrospector
    ): SecurityFilterChain{

        //Jwt Stateless
//        httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer -> {
//            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
//        });
//        OAuth2LoginAuthenticationFilter
        httpSecurity.addFilterAt(filter, UsernamePasswordAuthenticationFilter::class.java)
        //        HttpSessionOAuth2AuthorizationRequestRepository
        httpSecurity.authorizeHttpRequests {
            it.requestMatchers(
                MvcRequestMatcher(introspect, "/**")
            ).permitAll().anyRequest().authenticated()
        }
        httpSecurity.formLogin { it.loginPage(
                loginRequestUrl
            ).defaultSuccessUrl("/")
        }
        httpSecurity.logout { it.logoutRequestMatcher(AntPathRequestMatcher(logoutRequestUrl))
                .deleteCookies("refresh-token") //Remove refresh Token
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
        }
        httpSecurity.csrf { it.disable() }

        //FIXME is the best way?
        //OAuth Client enabled.
        if (listOf(*environment.activeProfiles).contains("oauth-client")) {
            log.info("Oauth client enabled.")
            httpSecurity.oauth2Login { it.userInfoEndpoint { config -> config.userService(userService) }
            }
        }
        return httpSecurity.build()
    }
}