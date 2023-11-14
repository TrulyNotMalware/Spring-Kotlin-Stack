package dev.notypie.jwt.utils

import dev.notypie.base.annotations.SpringIntegrationTest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import java.nio.charset.StandardCharsets
import java.util.Base64

@SpringIntegrationTest
@ActiveProfiles("jwt","test")
class JwtVerifierTest @Autowired constructor(
    private val verifier: JwtVerifier
) : BehaviorSpec ({

    given("[mod.common] JwtVerifier test"){
        val plainText = "THIS IS TEST PLAIN TEXT"
        val encrypt = verifier.sign(plainText)
        `when`("Decrypt token"){
            val decrypt: String = verifier.decrypt(encrypt)
            then("Successfully works"){
                plainText shouldBeEqual Base64.getDecoder().decode(decrypt).toString(StandardCharsets.UTF_8)
            }
        }
    }
})