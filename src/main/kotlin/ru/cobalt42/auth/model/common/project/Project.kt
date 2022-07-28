package ru.cobalt42.auth.model.common.project

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.cobalt42.auth.model.dictionary.ObjectStatus
import ru.cobalt42.auth.model.dictionary.organizationSuite.OrganizationSuite

@Document
data class Project(
    var uid: String = "",
    val name: String = "",
    val code: String = "",
    val country: String = "",
    val region: String = "",
    val city: String = "",
    val street: String = "",
    val house: String = "",
    val building: String = "",
    val postCode: String = "",
    val fullAddress: String = "",
    val hasProjectParts: Boolean = false,
    val copyCount: String = "",
    val defaultOrganizations: OrganizationSuite = OrganizationSuite(),
    val comment: String = "",
    val localization: String = "",
    val necessaryDocuments: NecessaryDocuments = NecessaryDocuments(),
    var objectStatus: ObjectStatus = ObjectStatus(),
    @JsonIgnore
    var objectInfo: ObjectInfo = ObjectInfo(),
    @JsonIgnore
    @Id
    var _id: ObjectId = ObjectId.get(),
)
