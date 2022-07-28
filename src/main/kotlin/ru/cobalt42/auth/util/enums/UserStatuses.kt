package ru.cobalt42.auth.util.enums

enum class UserStatuses(val status: Int) {
    DISABLED(0),
    ENABLED(1),
    EXPIRED(2),
}