package dev.notypie.exceptions

import dev.notypie.global.error.ArgumentError
import dev.notypie.global.error.exceptions.ErrorCode

class UserDomainException(
    private val errorCode: ErrorCode,
    val detail : List<ArgumentError> = listOf()
) : RuntimeException(errorCode.getMessage())