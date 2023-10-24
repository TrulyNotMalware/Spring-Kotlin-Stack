package dev.notypie.dao

import dev.notypie.domain.Users
import java.util.*

interface UsersRepository {
    fun findById(id: Long): Optional<Users>
    fun findByIdWithException(id: Long): Users
    fun findByUserIdWithException(userId: String): Users
    fun save(users: Users): Users
    fun updateRefreshToken(id: Long, refreshToken: String): Users
    fun findRefreshTokenById(id: Long): String
    fun saveOrUpdateByUserId(users: Users): Users
}