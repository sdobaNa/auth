package ru.cobalt42.auth.exception

data class ValidateException(
    val messages: List<ExceptionMessage>,
    val result: Any?
) : Exception()