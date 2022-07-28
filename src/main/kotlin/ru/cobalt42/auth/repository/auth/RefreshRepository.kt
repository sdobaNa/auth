package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.auth.Refresh

interface RefreshRepository : MongoRepository<Refresh, ObjectId> {
    fun getByToken(token: String): Refresh
    fun getByUserUid(user: String): Refresh
    fun deleteByUserUid(uid: String)
}