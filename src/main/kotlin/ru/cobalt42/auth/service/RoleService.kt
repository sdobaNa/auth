package ru.cobalt42.auth.service

import org.springframework.data.domain.Pageable
import ru.cobalt42.auth.dto.PaginatedResponse
import ru.cobalt42.auth.model.role.Role

interface RoleService {
    fun createOne(role: Role, authToken: String): Role
    fun getAll(paging: Pageable, search: String): PaginatedResponse
    fun getOne(uid: String): Role
    fun updateOne(uid: String, role: Role, authToken: String): Role
    fun deleteOne(uid: String)
}