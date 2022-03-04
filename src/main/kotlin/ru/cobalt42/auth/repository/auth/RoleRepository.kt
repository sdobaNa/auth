package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.auth.role.Role

interface RoleRepository : MongoRepository<Role, ObjectId> {
    fun getByUid(uid: String): Role
    fun getByNameContainingIgnoreCase(name: String, padding: Pageable): Page<Role>
    fun deleteByUid(uid: String)
}