package ru.cobalt42.auth.util

import org.json.JSONObject
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import ru.cobalt42.auth.exception.RequestException
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import java.util.*

@Component
class UserSearcher(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {

    fun isAdmin(authToken: String): Boolean {
        return try {
            userRepository.getByUid(getUserUid(authToken, "userUid")).superAdmin
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("User or role is missing", UNAUTHORIZED)
        }
    }

    fun isOriginalUser(authToken: String, userUid: String) = getUserUid(authToken, "userUid") == userUid

    private fun getUserUid(authToken: String, fieldName: String): String {
        val payload = try {
            String(
                Base64.getDecoder().decode(
                    authToken.split(".")[1]
                )
            )
        } catch (e: IllegalArgumentException) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        }
        return try {
            JSONObject(payload)[fieldName].toString()
        } catch (e: Throwable) {
            throw RequestException("System error", INTERNAL_SERVER_ERROR)
        }
    }
}