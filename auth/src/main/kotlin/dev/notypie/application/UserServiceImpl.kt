package dev.notypie.application

import dev.notypie.dao.UsersRepository
import dev.notypie.domain.Address
import dev.notypie.domain.Users
import dev.notypie.dto.UserRegisterDto
import dev.notypie.jwt.dto.UserDto
import dev.notypie.jwt.utils.logger
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val repository: UsersRepository
) : UserCRUDService, UserDetailsService{
    private val log = logger()

    override fun register(userRegisterDto: UserRegisterDto): UserDto =
        this.repository.save(
            Users(userId = userRegisterDto.userId,
                userName = userRegisterDto.userName,
                password = userRegisterDto.password,
                email = userRegisterDto.email,
                phoneNumber = userRegisterDto.phoneNumber,
                address = Address(
                    city = userRegisterDto.city,
                    streetAddress = userRegisterDto.streetAddress,
                    country = userRegisterDto.country,
                    region = userRegisterDto.region,
                    zipCode = userRegisterDto.zipCode
                )
            )
        ).toUserDto()

    override fun loadUserByUsername(userId: String): UserDetails
    = this.repository.findByUserIdWithException(userId = userId).createUserSecurity()

}