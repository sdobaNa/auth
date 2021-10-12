package ru.cobalt42.auth.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Refresh(
    var refresh: String = "",
    var exp: String = "",
    var token: String = "",
    var user: String = "",
    @Id
    @JsonIgnore
    var _id: ObjectId = ObjectId.get()

)