package ru.cobalt42.auth.model.role

data class Permission(
    val permissionLevel: Int = 0,
    val uname: String = "",
    val summary: String = ""
)
