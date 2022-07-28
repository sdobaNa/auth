package ru.cobalt42.auth.model.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Refresh(
    var userUid: String = "",
    var token: String = "",
    @JsonIgnore
    @Id
    var _id: ObjectId = ObjectId.get()

)