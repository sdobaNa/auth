package ru.cobalt42.auth.exception

import org.springframework.http.HttpStatus

class RequestException(override val message: String, private val httpStatus: HttpStatus) : Exception(message) {
    private val serialVersionUID = 1L

    fun getHttpStatus(): HttpStatus {
        return httpStatus
    }

    fun message(): String {
        return message
    }
}