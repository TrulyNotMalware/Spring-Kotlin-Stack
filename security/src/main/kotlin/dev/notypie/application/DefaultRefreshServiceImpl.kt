package dev.notypie.application

import dev.notypie.dao.UserRepository
import dev.notypie.jwt.utils.logger
import org.springframework.stereotype.Service

@Service
class DefaultRefreshServiceImpl(
    private val userRepository: UserRepository,

    ) {
    private val log = logger()
}