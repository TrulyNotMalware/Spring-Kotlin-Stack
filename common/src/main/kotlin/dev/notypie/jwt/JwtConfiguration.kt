package dev.notypie.jwt

import dev.notypie.jwt.utils.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration//Require All-Open
@Profile("jwt")
class JwtConfiguration(
    @Value("\${jwt.token.keystore.classpath}") private val keyStorePath: String,
    @Value("\${jwt.token.keystore.password}") private val keyStorePassword: String,
    @Value("\${jwt.token.key.alias}") private val keyAlias: String,
    @Value("\${jwt.token.key.privateKeyPassPhrase}") private val privateKeyPassphrase: String
) {
    private val log = logger()

    @Bean
    fun keyStore() : KeyStore{
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val resourceAsStream = Thread.currentThread().contextClassLoader.getResourceAsStream(this.keyStorePath)
        keyStore.load(resourceAsStream, this.keyStorePassword.toCharArray())
        return keyStore
    }

    @Bean
    fun rsaPrivateKey(keyStore: KeyStore): RSAPrivateKey{
        try{
            val key = keyStore.getKey(this.keyAlias, this.privateKeyPassphrase.toCharArray())
            if( key is RSAPrivateKey ) return key
            else throw IllegalArgumentException("Unable to load private key")
        } catch ( e: Exception ) {
            when(e) {
                is UnrecoverableKeyException, is NoSuchAlgorithmException, is KeyStoreException -> {
                    log.error("Unable to load private key from keystore: {}", this.keyStorePath, e)
                }
            }
            throw e
        }
    }

    @Bean
    fun rsaPublicKey(keyStore: KeyStore): RSAPublicKey{
        try{
            val certificate = keyStore.getCertificate(this.keyAlias)
            val publicKey = certificate.publicKey
            if (publicKey is RSAPublicKey) return publicKey
        }catch (e: KeyStoreException){
            log.error("Unable to load public key from keystore: {}", this.keyStorePath, e)
        }
        throw IllegalArgumentException("Unable to load RSA public key")
    }
}