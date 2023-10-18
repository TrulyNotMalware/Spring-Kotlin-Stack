package dev.notypie.jwt.utils

import io.jsonwebtoken.Claims
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

@Service
@Profile("jwt")
class RSAVerifier(
    private val privateKey: PrivateKey,
    private val publicKey: PublicKey
) : JwtVerifier{

    // FIXME Fix Signature verify.
    override fun verifySignature(plainText: String, signature: String): Boolean {
        try {
            val mySignature = Signature.getInstance("SHA256withRSA")
            mySignature.initVerify(this.publicKey)
            mySignature.update(plainText.toByteArray())
            if (!mySignature.verify(Base64.getDecoder().decode(signature))) throw RuntimeException("Signature invalid.")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: SignatureException) {
            throw RuntimeException(e)
        }
        return true
    }

    override fun sign(plainText: String): String {
        return try {
            val encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            encrypt.init(Cipher.ENCRYPT_MODE, this.privateKey) // Encrypt by this private key.
            Base64.getEncoder().encodeToString(encrypt.doFinal(plainText.toByteArray(StandardCharsets.UTF_8)))
        } catch (e: NoSuchPaddingException) {
            throw java.lang.RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw java.lang.RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw java.lang.RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw java.lang.RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw java.lang.RuntimeException(e)
        }
    }

    override fun decrypt(encryptedMessage: String): String {
        return try {
            val decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            decrypt.init(Cipher.DECRYPT_MODE, publicKey)
            Base64.getEncoder().encodeToString(decrypt.doFinal(Base64.getDecoder().decode(encryptedMessage)))
        } catch (e: NoSuchPaddingException) {
            throw java.lang.RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw java.lang.RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw java.lang.RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw java.lang.RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw java.lang.RuntimeException(e)
        }
    }

    override fun userParser(claims: Claims): Map<String, Any> {
        val userParseInfo: MutableMap<String, Any> = ConcurrentHashMap()
        userParseInfo["userId"] = claims.subject
        userParseInfo["roles"] = claims.get("roles", MutableList::class.java)
        userParseInfo["isExpired"] = !claims.expiration.before(Date())
        return userParseInfo
    }
}