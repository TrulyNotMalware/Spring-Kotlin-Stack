package dev.notypie.dao

import dev.notypie.domain.Users
import dev.notypie.exceptions.UserDomainException
import dev.notypie.exceptions.UserErrorCodeImpl
import dev.notypie.global.error.ArgumentError
import dev.notypie.jwt.utils.logger
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersRepositoryImpl (
    private val userRepository: UserRepository
): UsersRepository {
    private val log = logger()
    override fun findById(id: Long): Optional<Users> = this.userRepository.findById(id);


    override fun findByIdWithException(id: Long): Users {
        val errors: ArrayList<ArgumentError> = arrayListOf()
        errors.add(ArgumentError("id",id.toString(),"id not found in repository."))
        return this.userRepository.findById(id).orElseThrow { UserDomainException(UserErrorCodeImpl.USER_NOT_FOUND, errors) }
    }

    override fun findByUserIdWithException(userId: String): Users {
        val errors: ArrayList<ArgumentError> = arrayListOf()
        errors.add(ArgumentError("User Id",userId,"User id not found in repository."))
        return this.userRepository.findByUserId(userId).orElseThrow { UserDomainException(UserErrorCodeImpl.USER_NOT_FOUND, errors) }
    }

    override fun save(users: Users): Users = this.userRepository.save(users)

    override fun updateRefreshToken(id: Long, refreshToken: String?): Users
    = this.userRepository.save(this.findByIdWithException(id).updateRefreshToken(refreshToken))

    override fun findRefreshTokenById(id: Long): String? = this.findByIdWithException(id).getRefreshToken()

    override fun saveOrUpdateByUserId(users: Users): Users {
        log.info("Users : {}",users)
        this.userRepository.findByUserId(users.userId)
            .map { findUser -> findUser.updateUsers(users) }
            .orElse(users)
        return this.userRepository.save(users)
    }

}