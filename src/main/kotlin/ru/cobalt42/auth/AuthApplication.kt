package ru.cobalt42.auth

import com.mongodb.ConnectionString
import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import ru.cobalt42.auth.model.auth.Refresh
import ru.cobalt42.auth.model.auth.group.Group
import ru.cobalt42.auth.model.auth.role.Role
import ru.cobalt42.auth.model.auth.user.User
import ru.cobalt42.auth.util.enums.Permissions.PERMISSIONS
import ru.cobalt42.auth.util.enums.UserStatuses.*
import java.util.*

@SpringBootApplication
class AuthApplication

fun main(args: Array<String>) {
    val ctx = runApplication<AuthApplication>(*args)
    val host: String? = ctx.environment.getProperty("spring.data.mongodb.host")
    val port: String? = ctx.environment.getProperty("spring.data.mongodb.port")

    val databaseFactory = SimpleMongoClientDatabaseFactory(ConnectionString("mongodb://${host}:${port}/auth"))
    val converter = MappingMongoConverter(databaseFactory, MongoMappingContext())
    converter.setTypeMapper(DefaultMongoTypeMapper(null))

    MongoTemplate(databaseFactory, converter).save(
        User(
            uid = "superPuperUidAdmina",
            login = "cobalt",
            password = "\$2a\$10\$2wggeB6Xl0tnHnMMOdd4vuANO/xcxd/h2iAZJCev48kgZ/gOeZMk.",
            name = "admin",
            roles = listOf("superPuperUidRoleAdmina"),
            statusId = ENABLED.status,
            groupUid = "a053c3bc-69f5-4b0d-8d96-12fd2442b731",
            _id = ObjectId("6139c83a235ced2377be4f28"),
            superAdmin = true
        ),
        "user"
    )
    MongoTemplate(databaseFactory, converter).save(
        Role(
            uid = "superPuperUidRoleAdmina",
            name = "admin",
            permissions = PERMISSIONS.permissions.map { it.copy(permissionLevel = 4) },
            _id = ObjectId("6139c83a235ced2377be4f26")
        ),
        "role"
    )
    MongoTemplate(databaseFactory, converter).save(
        Refresh(
            token = "",
            userUid = "superPuperUidAdmina",
            _id = ObjectId("6139c983235ced2377be534c")
        ),
        "refresh"
    )
    MongoTemplate(databaseFactory, converter).save(
        Group(
            uid = "a053c3bc-69f5-4b0d-8d96-12fd2442b731",
            name = "admin",
            _id = ObjectId("6139c983235ced3228be534c")
        ),
        "group"
    )
}
