package dev.notypie.global.error

import dev.notypie.global.error.exceptions.CommonErrorCodeImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleMethodArgumentException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        ResponseEntity(ErrorResponse(CommonErrorCodeImpl.INVALID_PARAMETER), CommonErrorCodeImpl.INVALID_PARAMETER.getStatus())

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    protected fun handleMethodArgsTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse>{
        val argumentErrors: ArrayList<ArgumentError> = arrayListOf()
        val value = if (e.value == null) "" else e.value.toString()
        argumentErrors.add(ArgumentError(e.name, value, e.errorCode))
        return ResponseEntity(ErrorResponse(CommonErrorCodeImpl.INVALID_ARGUMENT_TYPE, argumentErrors),
            CommonErrorCodeImpl.INVALID_ARGUMENT_TYPE.getStatus())
    }
}