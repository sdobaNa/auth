package ru.cobalt42.auth.config.mongo

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(
    basePackages = ["ru.cobalt42.auth.repository.common"],
    mongoTemplateRef = "commonMongoTemplate"
)
class CommonMongoConfig