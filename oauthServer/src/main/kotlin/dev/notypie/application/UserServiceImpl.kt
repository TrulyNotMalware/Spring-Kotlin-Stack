package dev.notypie.application

import dev.notypie.dao.UsersRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val repository: UsersRepository
): UserCRUDService, UserDetailsService{

    override fun loadUserByUsername(userId: String): UserDetails = this.repository.findByUserIdWithException(userId = userId).createUserSecurity()

}