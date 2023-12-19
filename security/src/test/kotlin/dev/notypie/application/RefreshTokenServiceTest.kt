package dev.notypie.application

import dev.notypie.base.annotations.SpringMockTest
import dev.notypie.builders.MockUserBuilders
import dev.notypie.dao.UsersRepository
import dev.notypie.domain.Users
import dev.notypie.jwt.dto.JwtDto
import dev.notypie.jwt.utils.JwtTokenProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@SpringMockTest
@ActiveProfiles("test","jwt")
class RefreshTokenServiceTest @Autowired constructor(
    private val refreshTokenService: RefreshTokenService,
    private val provider: JwtTokenProvider,
    private val userRepository: UsersRepository
): BehaviorSpec({
    given("[mod.Security] Token Generator Test"){
        val mockBuilder = MockUserBuilders()
        val user: Users = userRepository.save(mockBuilder.createDefaultUsers())
        val id = userRepository.findByUserIdWithException(user.userId).id
        val roles = mutableListOf<String>()
        roles.add("testRole")

        `when`("Generate new tokens"){
            val tokens: JwtDto = refreshTokenService.generateNewTokens(id=id, roles=roles)
            val refreshToken: String = tokens.refreshToken
            val accessToken: String = tokens.accessToken

            then("successfully create token"){
                provider.isExpiredToken(accessToken) shouldBe false
                provider.isExpiredToken(refreshToken) shouldBe false
                provider.validateJwtToken(accessToken) shouldBe true
                provider.validateJwtToken(refreshToken) shouldBe true
            }
        }
    }
})