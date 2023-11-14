package dev.notypie.jwt.utils

import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
@Profile("jwt")
class JwtTokenProvider(
    private val key: RSAPrivateKey,
    private val publicKey: RSAPublicKey,
    @Value("\${jwt.token.accessTokenExpiredTime}") private val ACCESS_TOKEN_EXPIRED: Long,
    @Value("\${jwt.token.refreshTokenExpiredTime}") private val REFRESH_TOKEN_EXPIRED: Long
) {
    private val log = logger()

    fun createJwtAccessToken(id: String, roles: List<String>): String{
        val claims = Jwts.claims().setSubject(id)
        claims["roles"] = roles

        val headers = ConcurrentHashMap<String, Any>()
        headers["alg"] = "RS256"
        headers["typ"] = "JWT"
        return Jwts.builder()
            .setHeader(headers)
            .addClaims(claims)
            .setExpiration(Date(System.currentTimeMillis()+ this.ACCESS_TOKEN_EXPIRED))
            .setIssuedAt(Date())
            .signWith(this.key, SignatureAlgorithm.RS256)
            .setIssuer("notypie_dev")
            .compact()
    }

    fun createJwtRefreshToken(): String{
        val claims = Jwts.claims()
        claims["value"] = UUID.randomUUID()
        val headers = ConcurrentHashMap<String, Any>()
        headers["alg"] = "RS256"
        headers["typ"] = "JWT"

        return Jwts.builder()
            .setHeader(headers)
            .addClaims(claims)
            .setExpiration(
                Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED)
            )
            .setIssuedAt(Date())
            .signWith(this.key, SignatureAlgorithm.RS256)
            .compact()
    }

    fun getClaimsFromJwtToken(token: String): Claims {
        try{
            return Jwts.parserBuilder().setSigningKey(this.publicKey).build().parseClaimsJws(token).body
        }catch (e: Exception) {
            when(e){
                is ExpiredJwtException -> {
                    this.log.error("Token Expired.")
                    return e.claims
                }
            }
            e.printStackTrace()
            throw e
        }
    }

    fun isExpiredToken(token: String): Boolean{
        return try {
            Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).body.expiration
            false
        } catch (e: ExpiredJwtException) {
            log.error("Expired JWT Tokens : {}", token)
            true
        }
    }

    fun validateJwtToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(this.publicKey).build().parseClaimsJws(token)
            true
        } catch (e: SignatureException) {
            log.error("Invalid JWT signature: {}", e.message)
            false
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT token: {}", e.message)
            false
        } catch (e: ExpiredJwtException) {
            log.error("JWT token is expired: {}", e.message)
            false
        } catch (e: UnsupportedJwtException) {
            log.error("JWT token is unsupported: {}", e.message)
            false
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: {}", e.message)
            false
        }
    }

    fun equalRefreshTokenId(findRefreshToken: String, refreshToken: String): Boolean {
        val refreshTokenId = this.getClaimsFromJwtToken(findRefreshToken)["value"].toString()
        val compareTokenId = getClaimsFromJwtToken(refreshToken)["value"].toString()
        return refreshTokenId == compareTokenId
    }
}