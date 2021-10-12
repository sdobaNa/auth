package ru.cobalt42.auth.service

import org.springframework.data.domain.Pageable
import ru.cobalt42.auth.model.User

interface UserService {
    fun createOne(user: User, authToken: String): User
    fun getAll(paging: Pageable, search: String)
    fun getOne(uid: String): User
    fun updateOne(uid: String, user: User, authToken: String): User
    fun deleteOne(uid: String)
}