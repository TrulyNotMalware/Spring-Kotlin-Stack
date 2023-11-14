package dev.notypie.jwt.utils

import dev.notypie.base.annotations.SpringIntegrationTest
import dev.notypie.builders.JwtTokenBuilder
import io.jsonwebtoken.security.SignatureException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@SpringIntegrationTest
@ActiveProfiles("jwt","test")
class JwtTokenProviderTest @Autowired constructor(
    private val provider: JwtTokenProvider,
    private val privateKey: RSAPrivateKey,
    private val publicKey: RSAPublicKey
): BehaviorSpec({


    given("[mod.common] Jwt provider verification"){
        val id = "IamTestId"
        val roles = listOf("User")
        val accessToken = provider.createJwtAccessToken(id = id, roles = roles)
        val refreshToken = provider.createJwtRefreshToken()
        val keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
        val builder = JwtTokenBuilder(keyPair = keyPair)
        val expiredTokenBuilder = JwtTokenBuilder(privateKey = privateKey)

        `when`("Parse claims"){
            val claim = provider.getClaimsFromJwtToken(accessToken)
            val refreshClaim = provider.getClaimsFromJwtToken(refreshToken)

            then("claims successfully parsed"){
                claim.subject shouldBe id
                claim["roles"] shouldBe roles
                refreshClaim["value"] shouldNotBe null
            }
        }

        `when`("Validate token"){
            val invalidSignature = builder.buildRefreshToken(0)
            val malformedJwtToken = "I AM MALFORMED TOKEN"
            val malformedJwtToken2 = "easd.easd.eazzxx"
            val expiredToken: String = expiredTokenBuilder.buildRefreshToken(0)

            val invalidSign: Boolean = provider.validateJwtToken(invalidSignature)
            val malformedToken: Boolean = provider.validateJwtToken(malformedJwtToken)
            val malformedToken2: Boolean = provider.validateJwtToken(malformedJwtToken2)
            val jwtExpiredException: Boolean = provider.validateJwtToken(expiredToken)

            then("successfully works"){
                invalidSign shouldBe false
                malformedToken shouldBe false
                malformedToken2 shouldBe false
                jwtExpiredException shouldBe false
            }
        }

        `when`("Check refresh token id"){
            val unknownSignatureToken: String = builder.buildRefreshToken(2000)
            val expiredRefreshToken: String = expiredTokenBuilder.buildRefreshToken(0)
            //when
            val isOk = provider.equalRefreshTokenId(refreshToken, refreshToken)
            val isEqualValue = provider.equalRefreshTokenId(expiredRefreshToken, expiredRefreshToken) //This will be OK.
            val isNotEqual = provider.equalRefreshTokenId(refreshToken, expiredRefreshToken)

            then("Extract UUID successfully work"){
                shouldThrowExactly<SignatureException> { provider.equalRefreshTokenId(findRefreshToken = unknownSignatureToken,refreshToken = refreshToken) }
                isOk shouldBe true
                isEqualValue shouldBe true
                isNotEqual shouldBe false
            }
        }
    }
})