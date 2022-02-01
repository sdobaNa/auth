package ru.cobalt42.auth.exception

import com.fasterxml.jackson.annotation.JsonIgnore

data class ValidateException(
    val result: Any?,
    val messages: List<ExceptionMessage>,
    @JsonIgnore
    override val message: String = "",
    @JsonIgnore
    val stackTrace: List<Exception> = emptyList(),
    @JsonIgnore
    val suppressed: List<Exception> = emptyList(),
    @JsonIgnore
    val localizedMessage: Exception = Exception(),
    @JsonIgnore
    override val cause: Exception = Exception(),
) : Exception()