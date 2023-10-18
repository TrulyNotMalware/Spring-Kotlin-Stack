package dev.notypie.jwt.utils

import io.jsonwebtoken.Claims


interface JwtVerifier {
    fun verifySignature(plainText: String, signature: String): Boolean
    fun sign(plainText: String): String
    fun decrypt(encryptedMessage: String): String
    fun userParser(claims: Claims): Map<String, Any>
}
