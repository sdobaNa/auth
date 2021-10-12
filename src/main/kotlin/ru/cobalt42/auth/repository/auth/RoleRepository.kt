package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.role.Role

interface RoleRepository : MongoRepository<Role, ObjectId> {
    fun findByUid(uid: String): Role
}