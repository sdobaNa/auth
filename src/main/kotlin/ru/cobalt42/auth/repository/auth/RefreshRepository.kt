package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.Refresh

interface RefreshRepository : MongoRepository<Refresh, ObjectId> {
    fun getByRefresh(refresh: String): Refresh
    fun getByUser(user: String): Refresh
}