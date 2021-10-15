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
import ru.cobalt42.auth.model.Refresh
import ru.cobalt42.auth.model.User
import ru.cobalt42.auth.model.role.Role
import ru.cobalt42.auth.util.enums.Permissions.PERMISSIONS
import java.text.SimpleDateFormat
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

    fun getUid() = UUID.randomUUID().toString()
    val userUid = getUid()
    val refreshUid = getUid()
    val roleUid = getUid()

    MongoTemplate(databaseFactory, converter).save(
        User(
            uid = userUid,
            comment = "",
            disabled = false,
            login = "cobalt",
            password = "\$2a\$10\$2wggeB6Xl0tnHnMMOdd4vuANO/xcxd/h2iAZJCev48kgZ/gOeZMk.",
            personUid = "",
            roles = listOf(roleUid),
            _id = ObjectId("6139c83a235ced2377be4f28")
        ),
        "user"
    )
    MongoTemplate(databaseFactory, converter).save(
        Role(
            uid = roleUid,
            comment = "",
            name = "admin",
            permissions = PERMISSIONS.permissions.map { it.copy(permissionLevel = 4) },
            _id = ObjectId("6139c83a235ced2377be4f26")
        ),
        "role"
    )
    MongoTemplate(databaseFactory, converter).save(
        Refresh(
            refresh = refreshUid,
            exp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date(System.currentTimeMillis() + 36000000)),
            token = "",
            user = userUid,
            _id = ObjectId("6139c983235ced2377be534c")
        ),
        "refresh"
    )
}
