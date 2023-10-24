package dev.notypie.dao

import dev.notypie.domain.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<Users, Long> {
    fun findByUserId(userId: String) : Optional<Users>
}