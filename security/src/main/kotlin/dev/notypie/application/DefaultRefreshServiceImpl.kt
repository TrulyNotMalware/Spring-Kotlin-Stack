package dev.notypie.application

import dev.notypie.dao.UsersRepository
import dev.notypie.domain.Users
import dev.notypie.exceptions.UserDomainException
import dev.notypie.exceptions.UserErrorCodeImpl
import dev.notypie.global.error.ArgumentError
import dev.notypie.jwt.dto.JwtDto
import dev.notypie.jwt.utils.CookieProvider
import dev.notypie.jwt.utils.JwtTokenProvider
import dev.notypie.jwt.utils.logger
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import kotlin.math.exp

@Profile("jwt")
@Service
class DefaultRefreshServiceImpl(
    private val repository: UsersRepository,
    private val tokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService,
    private val cookieProvider: CookieProvider
    ): RefreshTokenService {
    private val log = logger()
    override fun updateRefreshToken(id: Long, refreshToken: String?){
        this.repository.updateRefreshToken(id, refreshToken)
    }

    override fun isDuplicateRefreshToken(id: Long): Boolean = this.repository.findRefreshTokenById(id) != null

    override fun refreshJwtToken(accessToken: String, refreshToken: String): JwtDto {
        val id: Long = this.tokenProvider.getClaimsFromJwtToken(accessToken).subject.toLong()
        val user: Users = this.repository.findByIdWithException(id)
        val findRefreshToken: String = user.getRefreshToken() ?: throw UserDomainException(UserErrorCodeImpl.REFRESH_TOKEN_NOT_EXISTS)
        // 10.31 show more detail exceptions.
        val errors: ArrayList<ArgumentError> = arrayListOf()
        if(!this.tokenProvider.isExpiredToken(accessToken))
            errors.add(ArgumentError("access token",accessToken,"Access Token is not expired yet. This will be reported"))
        if(!this.tokenProvider.validateJwtToken(refreshToken))
            errors.add(ArgumentError("refresh token",refreshToken,"Refresh Token is not valid. This will be reported"));
        if(!this.tokenProvider.equalRefreshTokenId(findRefreshToken, refreshToken))
            errors.add(ArgumentError("Refresh Token","NO_DETAIL","Refresh Token is not valid. This will be reported"));
        if(errors.isNotEmpty()){
            this.repository.updateRefreshToken(id, null)
            log.error("errors : {}", errors)
            throw UserDomainException(UserErrorCodeImpl.TOKEN_REISSUE_FAILED, errors)
        }
        //Authentication success.
        val authentication: Authentication = getAuthentication(user.userId)
        val roles: List<String> = authentication.authorities
            .map{ grantedAuthority -> grantedAuthority.authority }.toList()
        val newAccessToken = this.tokenProvider.createJwtAccessToken(id.toString(), roles)
        val expiredTime = tokenProvider.getClaimsFromJwtToken(newAccessToken).expiration
        return JwtDto(accessToken = newAccessToken, refreshToken = refreshToken, accessTokenExpiredDate = expiredTime)
    }

    override fun generateNewTokens(id: Long, roles: List<String>): JwtDto {
        val accessToken = this.tokenProvider.createJwtAccessToken(id.toString(), roles)
        val refreshToken = this.tokenProvider.createJwtRefreshToken()
        val expiredTime = tokenProvider.getClaimsFromJwtToken(accessToken).expiration
        return JwtDto(accessToken = accessToken, refreshToken = refreshToken, accessTokenExpiredDate = expiredTime)
    }

    override fun logoutToken(accessToken: String): ResponseCookie {
        if(!this.tokenProvider.validateJwtToken(accessToken)) throw UserDomainException(UserErrorCodeImpl.INVALID_ACCESS_TOKEN)
        val id : Long = this.tokenProvider.getClaimsFromJwtToken(accessToken).subject.toLong()
        this.repository.updateRefreshToken(id, null)
        return this.cookieProvider.removeRefreshTokenCookie()
    }

    override fun createRefreshToken(refreshToken: String): ResponseCookie
    = this.cookieProvider.createRefreshTokenCookie(refreshToken = refreshToken)

    private fun getAuthentication(userId: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(userId)
        return UsernamePasswordAuthenticationToken(userDetails, userDetails.password, userDetails.authorities)
    }
}