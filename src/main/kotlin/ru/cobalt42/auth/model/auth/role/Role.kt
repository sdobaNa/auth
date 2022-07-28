package ru.cobalt42.auth.model.auth.role

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Role(
    var uid: String = "",
    var name: String = "",
    var permissions: List<Permission> = emptyList(),
    @JsonIgnore
    @Id
    var _id: ObjectId = ObjectId.get()
)
