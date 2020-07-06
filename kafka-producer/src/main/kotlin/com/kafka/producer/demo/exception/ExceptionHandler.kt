package com.kafka.producer.demo.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

private fun String.beautify() = this.removeSuffix("]").removePrefix("[")

@RestControllerAdvice
class ExceptionHandler {

    data class Information(val status: HttpStatus, val code: Int, val message: String)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleIllegalArgumentException(exception: IllegalArgumentException) =
        Information(BAD_REQUEST, BAD_REQUEST.value(), exception.toString())

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException) =
        Information(
            BAD_REQUEST,
            BAD_REQUEST.value(),
            exception.bindingResult.allErrors.map { it.defaultMessage }.joinToString(separator = ", ")
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException) =
        Information(BAD_REQUEST, BAD_REQUEST.value(), exception.localizedMessage.beautify())
}
