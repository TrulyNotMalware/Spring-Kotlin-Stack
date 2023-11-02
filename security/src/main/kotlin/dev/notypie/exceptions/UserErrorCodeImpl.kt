package dev.notypie.exceptions

import dev.notypie.global.error.exceptions.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCodeImpl(
    private val status: HttpStatus,
    private val message: String
) : ErrorCode {
    USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "User Not Found"),
    REFRESH_TOKEN_NOT_EXISTS(HttpStatus.UNAUTHORIZED, "RefreshToken is empty."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Access token is not valid."),
    TOKEN_REISSUE_FAILED(HttpStatus.UNAUTHORIZED, "Reissue failed."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "Authentication failed.");

    override fun getMessage(): String = this.message
    override fun getStatus(): HttpStatus = this.status
}