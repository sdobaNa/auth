package ru.cobalt42.auth

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.RefreshData
import ru.cobalt42.auth.model.Refresh
import ru.cobalt42.auth.model.User
import ru.cobalt42.auth.model.role.Role
import ru.cobalt42.auth.repository.auth.RefreshRepository
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.enums.Permissions
import java.util.*
import javax.annotation.PostConstruct

@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthorizationTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var refreshRepository: RefreshRepository

    @Autowired
    private lateinit var roleRepository: RoleRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @LocalServerPort
    private var port = 0

    private fun getPathGenerate() = "http://localhost:${port}/api/auth/generate"
    private fun getPathRefresh() = "http://localhost:${port}/api/auth/refresh"

    @PostConstruct
    private fun save() {
        userRepository.save(
            User(
                uid = "0bf6cd3f-c3d3-45b7-b6ff-7a5122411916",
                login = "cobalt",
                password = "$2a$10$2wggeB6Xl0tnHnMMOdd4vuANO/xcxd/h2iAZJCev48kgZ/gOeZMk.",
                roles = listOf("ff5084b6-bcf2-43fd-beff-d47bbf4610b8")
            )
        )
        roleRepository.save(
            Role(
                uid = "ff5084b6-bcf2-43fd-beff-d47bbf4610b8",
                name = "admin",
                permissions = Permissions.PERMISSIONS.permissions.map { it.copy(permissionLevel = 4) },
            )
        )
        refreshRepository.save(
            Refresh(
                refresh = UUID.randomUUID().toString(),
                exp = "2021-10-27T21:46:34+0500",
                token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwZXJtaXNzaW9uIjp7InBlcnNvbiI6N...",
                user = "0bf6cd3f-c3d3-45b7-b6ff-7a5122411916"
            )
        )
    }

    @Test
    fun generateToken() {
        val badLoginRequest = restTemplate.exchange(
            getPathGenerate(),
            HttpMethod.POST,
            HttpEntity(Authorization("test", "fuybz<fhnj")),
            DefaultResponse::class.java
        )
        assertEquals(400, badLoginRequest.statusCodeValue)

        val badPasswordRequest = restTemplate.exchange(
            getPathGenerate(),
            HttpMethod.POST,
            HttpEntity(Authorization("cobalt", "test")),
            DefaultResponse::class.java
        )
        assertEquals(400, badPasswordRequest.statusCodeValue)

        val correctResponse = restTemplate.exchange(
            getPathGenerate(),
            HttpMethod.POST,
            HttpEntity(Authorization("cobalt", "fuybz<fhnj")),
            DefaultResponse::class.java
        )
        assertEquals(200, correctResponse.statusCodeValue)

        val request = try {
            ObjectMapper().convertValue(
                correctResponse.body?.result,
                object : TypeReference<RefreshData>() {})
        } catch (e: IllegalArgumentException) {
            assert(false) { "not deserializable object" }
            RefreshData()
        }

        val badTokenResponse = restTemplate.exchange(
            getPathRefresh(),
            HttpMethod.POST,
            HttpEntity(
                RefreshData(
                    "b7eac3a1-54db-4526-a6b4-6e2cb0d617e4",
                    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwZXJtaXNzaW9uIjp7InBlcnNvbiI6NCwiaW5jb21pbmdDb250cm9sIjo0LCJxdWFsaXR5RG9jdW1lbnQiOjQsInR1YmVEb2N1bWVudFBhY2siOjQsInByb2plY3QiOjQsImRyYXdpbmciOjQsImRyYXdpbmdSZXZpc2lvbiI6NCwicHJvamVjdFBhcnQiOjQsImNvbnN0cnVjdGlvbiI6NCwicGxvdCI6NCwiY29uc3VtYWJsZSI6NCwidHViZUxpbmVQYXJ0Ijo0LCJ0dWJlTGluZSI6NCwiam9pbnRUdWJlTGluZVBhcnQiOjQsIndlbGRlckFkbWlzc2lvblNoZWV0Ijo0LCJuYWtzIjo0LCJ3ZWxkZXJTa2lsbCI6NCwid2VsZGluZ0pvdXJuYWwiOjQsImxhYkNvbmNsdXNpb24iOjQsInBvc2l0aW9uIjo0LCJ1c2VyIjo0LCJyb2xlIjo0LCJmaWxlIjo0LCJkZWJpdENvbW1vZGl0aWVzIjo0LCJleGVjdXRpdmVTY2hlbWUiOjQsIm9yZ2FuaXphdGlvbiI6NCwic3BlY2lmaWNhdGlvbiI6NCwic3BlY2lmaWNhdGlvblJldmlzaW9uIjo0LCJ0ZWNobm9sb2dpY2FsTm9kZSI6NCwid29yayI6NCwiZWxlY3Ryb2RlQXBwcm92YWwiOjQsImVxdWlwbWVudEFmdGVyQ29tcGxleFRlc3RBY3QiOjQsImVxdWlwbWVudEFmdGVySW5kaXZpZHVhbFRlc3RBY3QiOjQsImVxdWlwbWVudERlZmVjdHNBY3QiOjQsImVxdWlwbWVudEluc3RhbGxhdGlvbk9uRm91bmRhdGlvbkFjdCI6NCwiaGlkZGVuV29ya0FjdCI6NCwibWVjaGFuaXNtVGVzdEFjdCI6NCwicGFzc2luZ0VxdWlwbWVudFRvSW5zdGFsbGF0aW9uQWN0Ijo0LCJwcm90ZWN0aXZlQ29hdGluZ0FjdCI6NCwicmVzcG9uc2libGVTdHJ1Y3R1cmVBY3QiOjQsInN0cmV0Y2hpbmdDb21wZW5zYXRvckFjdCI6NCwidmVzc2VsQXBwYXJhdHVzVGVzdEFjdCI6NCwidGVjaG5vbG9naWNhbENhcmQiOjQsImNlcnRpZmljYXRlIjo0LCJ0dWJlTGluZVRlc3RQZXJtaXQiOjQsImNsZWFuaW5nRGV2aWNlIjo0LCJtZWFzdXJpbmdJbnN0cnVtZW50Ijo0LCJqb2ludENvbmNsdXNpb24iOjQsIndvcmtDb25jbHVzaW9uIjo0LCJzdG9UZXN0QWN0Ijo0LCJ0dWJlTGluZURyeWluZ1Blcm1pdCI6NCwidHViZUxpbmVEcnlpbmdBY3QiOjQsIm5pdHJvZ2VuRmlsbGluZ0FjdCI6NCwiaW5zdGFsbGVkRXF1aXBtZW50QWN0Ijo0LCJzdGFmZk9yZGVyIjo0LCJwb3J0YWJsZUVxdWlwbWVudCI6NCwiZmFzdGVuZXJQYXJ0Ijo0LCJjb2F0aW5nTWF0ZXJpYWwiOjQsImVxdWlwbWVudCI6NCwid2VsZGluZ0NvbnN1bWFibGUiOjQsInRoZXJtYWxJbnN1bGF0aW9uIjo0LCJ3YXliaWxsIjo0LCJwcm9qZWN0Q2hhbmdlbG9nIjowfSwiZXhwIjoxNjM0MDQwNTk3LCJ1c2VyIjoiMGJmNmNkM2YtYzNkMy00NWI3LWI2ZmYtN2E1MTIyNDExOTE2IiwiaWF0IjoxNjM0MDM5Njk3fQ.rdFuL0zD3NWyHrtAFXXtWomN9_qfSSt8_D4Eun8ga-I"
                )
            ),
            DefaultResponse::class.java
        )
        assertEquals(401, badTokenResponse.statusCodeValue)

        val refreshResponse = restTemplate.exchange(
            getPathRefresh(),
            HttpMethod.POST,
            HttpEntity(
                RefreshData(
                    request.refresh,
                    request.token
                )
            ),
            DefaultResponse::class.java
        )
        assertEquals(200, refreshResponse.statusCodeValue)
    }
}