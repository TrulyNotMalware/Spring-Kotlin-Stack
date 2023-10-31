package dev.notypie.global.error

import dev.notypie.global.error.exceptions.ErrorCode
import org.springframework.http.HttpStatus

class ErrorResponse (
    private val status: HttpStatus,
    private val message: String,
    private val detail: List<ArgumentError> = listOf(),
){
    constructor(errorCode: ErrorCode, argumentErrors: List<ArgumentError> = listOf())
            : this(errorCode.getStatus(), errorCode.getMessage(), argumentErrors)
}