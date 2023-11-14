package dev.notypie.builders

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

class JwtTokenBuilder(
    private val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair(),
    private val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey,
    private val publicKey: RSAPublicKey = keyPair.public as RSAPublicKey
) {

    fun buildAccessToken(id: String, roles: List<String>, accessTokenTimeout: Long): String{
        val claims: Claims = Jwts.claims()
        claims.subject = id
        claims["roles"] = roles

        val headers = mutableMapOf<String, Any>()
        headers["alg"] = "RS256"
        headers["typ"] = "JWT"
        return Jwts.builder().setHeader(headers)
            .addClaims(claims)
            .setExpiration(Date(System.currentTimeMillis() + accessTokenTimeout))
            .setIssuedAt(Date())
            .signWith(this.privateKey, SignatureAlgorithm.RS256)
            .setIssuer("tester")
            .compact()
    }

    fun buildRefreshToken(refreshTokenTimeout: Long): String {
        val claims = Jwts.claims()
        claims["value"] = UUID.randomUUID()
        val headers = mutableMapOf<String, Any>()
        headers["alg"] = "RS256"
        headers["typ"] = "JWT"
        return Jwts.builder()
            .setHeader(headers)
            .addClaims(claims)
            .setExpiration(
                Date(System.currentTimeMillis() + refreshTokenTimeout)
            )
            .setIssuedAt(Date())
            .signWith(this.privateKey, SignatureAlgorithm.RS256)
            .compact()
    }
}