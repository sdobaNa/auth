package ru.cobalt42.auth.model.auth.group

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Group(
    var uid: String = "",
    var name: String = "",
    var limits: Limits = Limits(),
    @JsonIgnore
    @Id
    var _id: ObjectId = ObjectId.get(),
)
