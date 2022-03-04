package ru.cobalt42.auth.repository.auth

import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.group.Group

interface GroupRepository : MongoRepository<Group, ObjectId> {
    fun findByUid(uid: String): Group
    fun findByNameContainingIgnoreCase(name: String, padding: Pageable): Page<Group>
    fun deleteByUid(uid: String)
}