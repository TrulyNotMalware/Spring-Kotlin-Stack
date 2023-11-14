package dev.notypie.application

import dev.notypie.base.annotations.SpringMockTest
import dev.notypie.builders.MockUserBuilders
import dev.notypie.dao.UsersRepository
import dev.notypie.domain.Users
import dev.notypie.exceptions.UserDomainException
import dev.notypie.jwt.dto.JwtDto
import dev.notypie.jwt.utils.JwtTokenProvider
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

//FIXME Change all of this to one annotation.
@SpringMockTest
@ActiveProfiles("test","jwt")
@TestPropertySource(properties = ["spring.config.location = classpath:application-expired-test.yaml"])
class ExpiredRefreshTokenServiceTest @Autowired constructor(
    private val refreshTokenService: RefreshTokenService,
    private val provider: JwtTokenProvider,
    private val userRepository: UsersRepository,
    private val service: InMemoryUserDetailsManager
): BehaviorSpec({

    given("[mod.Security] expired-refreshToken test") {
        val builder = MockUserBuilders(country = null, streetAddress = null, city = null, region = null)
        val user: Users = userRepository.save(builder.createDefaultUsers())
        val id: Long = userRepository.findByUserIdWithException(user.userId).id
        val roles: ArrayList<String> = arrayListOf()

        val authorities: ArrayList<SimpleGrantedAuthority> = arrayListOf()
        authorities.add(SimpleGrantedAuthority(user.getRole()))
        service.createUser(User(user.userId, user.password, authorities))
        val token: JwtDto = refreshTokenService.generateNewTokens(id = id, roles = roles)
        refreshTokenService.updateRefreshToken(id = id, refreshToken = token.refreshToken)

        `when`("valid access token entered"){
            val accessToken = token.accessToken
            then("successfully reissued") {
                val reissueToken = refreshTokenService.refreshJwtToken(
                    accessToken = accessToken, refreshToken = token.refreshToken
                )
                provider.isExpiredToken(accessToken) shouldBe true
                reissueToken.refreshToken shouldBeEqual token.refreshToken
                reissueToken.accessToken shouldNotBeEqual token.accessToken
            }
        }

        `when`("invalid access token entered"){
            val invalidToken = refreshTokenService.generateNewTokens(id = 10L, roles = roles)
            then("failed reissue access token"){
                shouldThrowExactly<UserDomainException> {
                    refreshTokenService.refreshJwtToken(invalidToken.accessToken, token.refreshToken)
                }
                val exception: UserDomainException = shouldThrowExactly<UserDomainException> {
                    refreshTokenService.refreshJwtToken(token.accessToken, invalidToken.refreshToken)
                }
                exception.detail shouldNotBe null
            }
        }
    }
})
