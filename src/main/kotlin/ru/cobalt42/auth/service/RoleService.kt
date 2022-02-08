package ru.cobalt42.auth.service

import org.springframework.data.domain.Pageable
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.model.auth.role.Role

interface RoleService {
    fun createOne(role: Role, authToken: String): DefaultResponse
    fun getAll(paging: Pageable, search: String): PaginatedResponse
    fun getOne(uid: String): DefaultResponse
    fun updateOne(uid: String, role: Role, authToken: String): DefaultResponse
    fun deleteOne(uid: String)
}