package ru.cobalt42.auth.exception

import ru.cobalt42.auth.model.dictionary.Target

data class ExceptionMessage(
    val code: Long = 0,
    val uname: String = "",
    val title: String = "",
    var description: String = "",
    var target: Target = Target(),
    var source: Target = Target(),
    var section: String = "",
)
