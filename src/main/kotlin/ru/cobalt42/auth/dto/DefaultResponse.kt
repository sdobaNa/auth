package ru.cobalt42.auth.dto

import ru.cobalt42.auth.exception.ExceptionMessage

data class DefaultResponse<T>(
    var result: T,
    var messages: MutableList<ExceptionMessage> = mutableListOf(),
)
