package ru.cobalt42.auth.dto

import ru.cobalt42.auth.model.auth.user.Logo

data class RefreshData(
    val refresh: String = "",
    val token: String = "",
    val uid: String = "",
    val userName: String = "",
    val position: String = "",
    val avatar: Logo = Logo()
)
