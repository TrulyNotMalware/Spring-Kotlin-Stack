package dev.notypie.dao

import dev.notypie.base.annotations.H2JpaRepositoryTest
import dev.notypie.builders.MockUserBuilders
import dev.notypie.domain.Users
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles

@H2JpaRepositoryTest
@ActiveProfiles("test")
class UserDaoTest @Autowired constructor(
    private val userRepository: UserRepository,
): BehaviorSpec({

    given("[mod.Security] Dao Test"){
        val domainRepository: UsersRepository = UsersRepositoryImpl(userRepository=userRepository)
        val mockBuilder = MockUserBuilders()
        val user: Users = userRepository.save(mockBuilder.createDefaultUsers())
        val id = userRepository.findByUserId(user.userId).orElseThrow().id

        `when`("find operator"){
            val findUser: Users = domainRepository.findByIdWithException(id=id)
            val findUserWithUserId: Users = domainRepository.findByUserIdWithException(userId = user.userId)

            then("Successfully find"){
                findUser.userId shouldBe user.userId
                findUserWithUserId.userId shouldBe user.userId

                findUser.password shouldBe user.password
                findUserWithUserId.password shouldBe user.password
            }
        }

        `when`("save operator"){
            val duplicateUser: Users = mockBuilder.createDefaultUsers()
            val newUser: Users = mockBuilder.createDefaultUsers(userId = "IAmNewUser")
            domainRepository.save(newUser)
            val selected: Users = domainRepository.findByUserIdWithException(newUser.userId)
            val listAll: List<Users> = userRepository.findAll()

            then("failed when duplicate unique key 'userId'"){
                shouldThrowExactly<DataIntegrityViolationException> { domainRepository.save(duplicateUser) }
            }
            then("Successfully saved"){
                selected shouldNotBe null
                listAll.size shouldBe 2

                selected.userId shouldBe newUser.userId
                selected.password shouldBe newUser.password
            }

        }
        //FIXME Add Test code
        `when`("update refresh token"){
            val newRefreshTokenValue = "I AM UPDATED!"
            val selected: Users = domainRepository.findByUserIdWithException(user.userId)
            selected.updateRefreshToken(newRefreshTokenValue)
            domainRepository.save(selected)
            val afterChanged: Users = domainRepository.findByUserIdWithException(user.userId)

            val newUser = mockBuilder.createDefaultUsers("helloNewUser")
            user.updateRefreshToken(newRefreshTokenValue)
            val updateUser = domainRepository.saveOrUpdateByUserId(user)
            val insertedUser = domainRepository.saveOrUpdateByUserId(newUser)

            then("Successfully update"){
                afterChanged.getRefreshToken() shouldBe newRefreshTokenValue
            }

            then("update or save successfully work"){
                updateUser.getRefreshToken() shouldBe newRefreshTokenValue
                insertedUser.userId shouldBe newUser.userId
            }
        }

    }
})