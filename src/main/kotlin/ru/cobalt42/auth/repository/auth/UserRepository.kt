package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.User

interface UserRepository : MongoRepository<User, ObjectId> {
    fun findByLogin(login: String): User
    fun findByUid(uid: String): User
}