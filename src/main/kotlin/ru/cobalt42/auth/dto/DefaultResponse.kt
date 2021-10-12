package ru.cobalt42.auth.dto

import ru.cobalt42.auth.model.exception.ExceptionMessage

data class DefaultResponse(
    var result: Any?,
    var messages: MutableList<ExceptionMessage> = mutableListOf()
)
