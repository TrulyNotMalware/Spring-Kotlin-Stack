package dev.notypie.application

import com.fasterxml.jackson.databind.ObjectMapper
import dev.notypie.exceptions.UserDomainException
import dev.notypie.exceptions.UserErrorCodeImpl
import dev.notypie.jwt.dto.LoginRequestDto
import dev.notypie.jwt.utils.CookieGenerator.of
import jakarta.annotation.PostConstruct
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
@Profile("jwt")
class LoginAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val refreshTokenService: RefreshTokenService,
    private val objectMapper: ObjectMapper,
    @Value("\${authentication.login.requestUrl}") private val loginRequestUrl: String
): UsernamePasswordAuthenticationFilter() {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val requestDto: LoginRequestDto = this.objectMapper.readValue(request.inputStream, LoginRequestDto::class.java)
        return this.authenticationManager.authenticate(UsernamePasswordAuthenticationToken(requestDto.userId, requestDto.password))
    }

    override fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication
    ) {
        val user = authResult.principal as User
        val roles = user.authorities.map { it.authority }.toList()
        val id: Long = user.username.toLong() // id로 지정했음.
        if(this.refreshTokenService.isDuplicateRefreshToken(id = id)){//Duplicate Login.
            this.refreshTokenService.updateRefreshToken(id, null)
            throw UserDomainException(UserErrorCodeImpl.AUTHENTICATION_FAILED)
        }
        val newToken = this.refreshTokenService.generateNewTokens(id = id, roles = roles)
        //Save refreshToken
        this.refreshTokenService.updateRefreshToken(id = id, newToken.refreshToken)
        val refreshTokenCookie = refreshTokenService.createRefreshToken(newToken.refreshToken)
        //[9.26] Module Changed to common, implementation change.
        val cookie = of(refreshTokenCookie)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.addCookie(cookie)
        val tokens = mapOf<String, Any>(
            "id" to id,
            "accessToken" to newToken.accessToken,
            "expiredTime" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newToken.accessTokenExpiredDate),
            "message" to "login success"
        )
        objectMapper.writeValue(response.outputStream, tokens)
    }

    @PostConstruct
    fun configure() {
        setAuthenticationManager(this.authenticationManager)
        setFilterProcessesUrl(loginRequestUrl)
        val contextRepository: SecurityContextRepository = HttpSessionSecurityContextRepository()
        setSecurityContextRepository(contextRepository)
    }
}