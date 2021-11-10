package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.role.Role
import ru.cobalt42.auth.model.user.User

interface RoleRepository : MongoRepository<Role, ObjectId> {
    fun findByUid(uid: String): Role
    fun findByNameContainingIgnoreCase(name: String, padding: Pageable): Page<User>
    fun deleteByUid(uid: String)
}