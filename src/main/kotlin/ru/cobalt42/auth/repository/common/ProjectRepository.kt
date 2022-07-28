package ru.cobalt42.auth.repository.common

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import ru.cobalt42.auth.model.common.project.Project

interface ProjectRepository: MongoRepository<Project, ObjectId> {
    fun getByUid(uid: String): Project
}