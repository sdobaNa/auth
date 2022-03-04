package ru.cobalt42.auth.repository.custom

import ru.cobalt42.auth.exception.ExceptionMessage

interface MainRepository {
    fun getMessages(authToken: String): Array<ExceptionMessage>
}