package ru.cobalt42.auth.model.auth.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    var uid: String = "",
    var firstName: String = "",
    var secondName: String = "",
    var lastName: String = "",
    var name: String = "",
    var position: String = "",
    var phoneNumber: String = "",
    var mail: String = "",
    var login: String = "",
    var password: String = "",
    var avatar: Logo = Logo(),
    var roles: List<String> = emptyList(),
    var subExpDate: String = "",
    var statusId: Int = 0,
    var groupUid: String = "",
    var projectUid: String = "",
    var organization: String = "",
    @JsonIgnore
    var superAdmin: Boolean = false,
    @JsonIgnore
    @Id
    var _id: ObjectId = ObjectId.get()
)
