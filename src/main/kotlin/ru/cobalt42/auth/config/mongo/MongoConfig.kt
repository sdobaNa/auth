package ru.cobalt42.auth.config.mongo

import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
class MongoConfig {
    @Autowired
    lateinit var mongoDbFactory: MongoDatabaseFactory

    @Autowired
    lateinit var mongoMappingContext: MongoMappingContext

    /*
    * Disable default TypeMapper
    * */
    @Bean
    fun mappingMongoConverter(): MappingMongoConverter {
        val dbRefResolver: DbRefResolver = DefaultDbRefResolver(mongoDbFactory)
        val converter = MappingMongoConverter(dbRefResolver, mongoMappingContext)
        converter.setTypeMapper(DefaultMongoTypeMapper(null))

        return converter
    }

    @Primary
    @Bean(name = ["authDocument"])
    @ConfigurationProperties(prefix = "mongodb.auth")
    fun getAuth(): MongoProperties = MongoProperties()

//    ---------

    @Primary
    @Bean
    fun authFactory(mongo: MongoProperties): MongoDatabaseFactory =
        SimpleMongoClientDatabaseFactory(MongoClients.create("mongodb://${mongo.host}:${mongo.port}"), mongo.database)

//    ---------

    @Primary
    @Bean(name = ["authMongoTemplate"])
    fun authMongoTemplate(): MongoTemplate = MongoTemplate(
        authFactory(getAuth()), mappingMongoConverter()
    )
}