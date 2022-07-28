package ru.cobalt42.auth.service

import org.springframework.data.domain.Pageable
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.model.auth.user.User

interface UserService {
    fun createOne(user: User, authToken: String): DefaultResponse<User>
    fun getAll(paging: Pageable, search: String): PaginatedResponse<User>
    fun getOne(uid: String, authToken: String): DefaultResponse<User>
    fun updateOne(uid: String, user: User, authToken: String): DefaultResponse<User>
    fun deleteOne(uid: String)
}