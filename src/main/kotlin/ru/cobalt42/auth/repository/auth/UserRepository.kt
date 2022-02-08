package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.auth.user.User

interface UserRepository : MongoRepository<User, ObjectId> {
    fun getByLogin(login: String): User
    fun getByUid(uid: String): User
    fun getByLoginContainingIgnoreCase(login: String, padding: Pageable): Page<User>
    fun deleteByUid(uid: String)
}