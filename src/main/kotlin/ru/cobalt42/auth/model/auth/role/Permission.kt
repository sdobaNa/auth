package ru.cobalt42.auth.model.auth.role

data class Permission(
    var permissionLevel: Int = 0,
    val uname: String = "",
    val summary: String = ""
)
