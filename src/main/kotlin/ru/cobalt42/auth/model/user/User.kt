package ru.cobalt42.auth.model.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    var uid: String = "",
    var disabled: Boolean = false,
    var firstName: String = "",
    var secondName: String = "",
    var lastName: String = "",
    var name: String = "",
    var organization: String = "",
    var position: String = "",
    var phoneNumber: String = "",
    var mail: String = "",
    var login: String = "",
    var password: String = "",
    var logo: Logo = Logo(),
    var roles: List<String> = emptyList(),
    @Id
    @JsonIgnore
    var _id: ObjectId = ObjectId.get()
)
