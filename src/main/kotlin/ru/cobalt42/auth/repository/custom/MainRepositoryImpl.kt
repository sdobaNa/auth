package ru.cobalt42.auth.repository.custom

import com.fasterxml.jackson.core.type.*
import org.springframework.beans.factory.annotation.*
import org.springframework.core.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.client.*
import ru.cobalt42.auth.exception.ExceptionMessage

@Repository
class MainRepositoryImpl : MainRepository {

    var restTemplate = RestTemplate()

    @Value("\${api.url}")
    lateinit var url: String

    @Value("\${api.refs}")
    lateinit var refsEndpoint: String

    override fun getMessages(authToken: String): Array<ExceptionMessage> = restTemplate.exchange(
        "$url$refsEndpoint/systemMessages",
        HttpMethod.GET,
        HttpEntity(null, HttpHeaders().also { header -> header.add("Authorization", authToken) }),
        Array<ExceptionMessage>::class.java
    ).body!!

}