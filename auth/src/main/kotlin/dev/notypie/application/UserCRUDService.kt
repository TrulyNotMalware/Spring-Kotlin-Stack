package dev.notypie.application

import dev.notypie.dto.UserRegisterDto
import dev.notypie.jwt.dto.UserDto

interface UserCRUDService {
    fun register(userRegisterDto: UserRegisterDto): UserDto
}