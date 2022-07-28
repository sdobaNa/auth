package ru.cobalt42.auth.dto

import ru.cobalt42.auth.exception.ExceptionMessage

data class PaginatedResponse<T>(
    var total: Number = 0,
    var result: List<T> = emptyList(),
    var messages: List<ExceptionMessage> = emptyList(),
)
