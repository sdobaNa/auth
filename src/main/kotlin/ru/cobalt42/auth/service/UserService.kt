package ru.cobalt42.auth.service

import org.springframework.data.domain.Pageable
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.model.user.User

interface UserService {
    fun createOne(user: User, authToken: String): User
    fun getAll(paging: Pageable, search: String): PaginatedResponse
    fun getOne(uid: String, authToken: String): User
    fun updateOne(uid: String, user: User, authToken: String): User
    fun deleteOne(uid: String)
}