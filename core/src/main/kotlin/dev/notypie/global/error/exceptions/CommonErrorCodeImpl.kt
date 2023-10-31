package dev.notypie.global.error.exceptions

import org.springframework.http.HttpStatus


enum class CommonErrorCodeImpl(
    private val status: HttpStatus,
    private val message: String
) : ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid Parameter included"),
    INVALID_ARGUMENT_TYPE(HttpStatus.BAD_REQUEST, "Invalid Parameter type included."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Request Method type is not allowed."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    SQL_CONNECTION_REFUSED(HttpStatus.INTERNAL_SERVER_ERROR, "Could not open connection for transaction.");

    override fun getMessage(): String = this.message;
    override fun getStatus(): HttpStatus = this.status;
}

