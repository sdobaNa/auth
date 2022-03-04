package ru.cobalt42.auth.dto

import ru.cobalt42.auth.exception.ExceptionMessage

data class DefaultResponse(
    var result: Any?,
    var messages: MutableList<ExceptionMessage> = mutableListOf()
)
