package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.user.User

interface UserRepository : MongoRepository<User, ObjectId> {
    fun findByLogin(login: String): User
    fun findByUid(uid: String): User
    fun findByNameContainingIgnoreCase(name: String, padding: Pageable): Page<User>
    fun deleteByUid(uid: String)
}