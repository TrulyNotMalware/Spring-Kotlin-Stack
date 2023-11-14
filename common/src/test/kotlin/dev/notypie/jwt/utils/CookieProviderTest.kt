package dev.notypie.jwt.utils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Duration


class CookieProviderTest: BehaviorSpec({

    given("[mod.common] CookieProvider"){
        val refreshTokenExpiredTime = "2000000";
        val cookieProvider = CookieProvider(refreshTokenExpiredTime = refreshTokenExpiredTime)
        val refreshToken = "ThisIsTestTokenValue"

        `when`("when create responseCookie"){
            val responseCookie = cookieProvider.createRefreshTokenCookie(refreshToken = refreshToken)

            then("successfully work as expected"){
                responseCookie.isHttpOnly shouldBe true
                responseCookie.isSecure shouldBe true

                responseCookie.path shouldNotBe null
                responseCookie.path!! shouldBeEqual "/"
                responseCookie.value shouldBeEqual refreshToken
                responseCookie.name shouldBe "refresh-token"
                responseCookie.maxAge shouldBe Duration.ofSeconds(refreshTokenExpiredTime.toLong())
            }

        }
        `when`("remove response cookie"){
            val responseCookie = cookieProvider.removeRefreshTokenCookie();

            then("successfully remove refresh token"){

                responseCookie.isHttpOnly shouldBe false
                responseCookie.isSecure shouldBe false
                responseCookie.path shouldNotBe null
                responseCookie.path!! shouldBeEqual "/"
                responseCookie.name shouldBe "refresh-token"
                responseCookie.maxAge shouldBe Duration.ofSeconds(0L)
            }
        }
    }
})