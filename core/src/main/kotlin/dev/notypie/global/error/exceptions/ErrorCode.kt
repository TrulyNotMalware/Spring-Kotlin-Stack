package dev.notypie.global.error.exceptions

import org.springframework.http.HttpStatus

interface ErrorCode {

    fun getStatus(): HttpStatus

    fun getMessage(): String
}