package ru.cobalt42.auth.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    var uid: String = "",
    var comment: String = "",
    var disabled: Boolean = false,
    var login: String = "",
    var password: String = "",
    var personUid: String = "",
    var roles: List<String> = emptyList(),
    @Id
    @JsonIgnore
    var _id: ObjectId = ObjectId.get()
)
