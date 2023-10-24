package dev.notypie.dao

import dev.notypie.domain.Users
import dev.notypie.jwt.utils.logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersRepositoryImpl (
    private val userRepository: UserRepository
): UsersRepository {
    private val log = logger()
    override fun findById(id: Long): Optional<Users> {
        return this.userRepository.findById(id);
    }

    override fun findByIdWithException(id: Long): Users {
        return this.userRepository.findById(id).orElseThrow { RuntimeException("userNotFound") }
    }

    override fun findByUserIdWithException(userId: String): Users {
        return this.userRepository.findByUserId(userId).orElseThrow { java.lang.RuntimeException("userNotFound") }
    }

    override fun save(users: Users): Users {
        return this.userRepository.save(users)
    }

    override fun updateRefreshToken(id: Long, refreshToken: String): Users {
        TODO("Not yet implemented")
    }

    override fun findRefreshTokenById(id: Long): String {
        TODO("Not yet implemented")
    }

    override fun saveOrUpdateByUserId(users: Users): Users {
        TODO("Not yet implemented")
    }

}