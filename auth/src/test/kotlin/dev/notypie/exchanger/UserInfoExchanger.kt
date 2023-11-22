package dev.notypie.exchanger

import dev.notypie.domain.Address
import dev.notypie.domain.Users
import dev.notypie.dto.UserRegisterDto
import dev.notypie.jwt.dto.LoginRequestDto
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserInfoExchanger {
    companion object{
        fun exchangeToUsers(dto: UserRegisterDto) =
            Users(
                userId = dto.userId,
                userName = dto.userName,
                password = BCryptPasswordEncoder().encode(dto.password),
                email = dto.email,
                phoneNumber = dto.phoneNumber,
                address = Address(
                    city = dto.city,
                    country = dto.country,
                    streetAddress = dto.streetAddress,
                    zipCode = dto.zipCode
                )
            )

        fun exchangeToLoginRequestDto(dto: UserRegisterDto) = LoginRequestDto(userId = dto.userId, password = dto.password)
    }
}