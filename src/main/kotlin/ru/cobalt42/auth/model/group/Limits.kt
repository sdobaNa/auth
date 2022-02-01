package ru.cobalt42.auth.model.group

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Limits(
    var projectLimit: Int = 0,
)
