package ru.cobalt42.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.json.JSONObject
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.RefreshData
import ru.cobalt42.auth.model.Refresh
import ru.cobalt42.auth.model.User
import ru.cobalt42.auth.model.exception.RequestException
import ru.cobalt42.auth.model.role.Role
import ru.cobalt42.auth.repository.auth.RefreshRepository
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.enums.Permissions.PERMISSIONS
import java.text.SimpleDateFormat
import java.util.*

@Service
class AuthorizationServiceImpl(
    private val userRepository: UserRepository,
    private val refreshRepository: RefreshRepository,
    private val roleRepository: RoleRepository,
) : AuthorizationService {
    override fun generate(authorization: Authorization): DefaultResponse {
        try {
            val user = userRepository.findByLogin(authorization.login)
            if (BCryptPasswordEncoder().matches(authorization.password, user.password)) {
                return generateJWT(user)
            } else {
                throw RequestException("Wrong password", BAD_REQUEST)
            }
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("Wrong login", BAD_REQUEST)
        }
    }

    override fun refresh(refreshData: RefreshData): DefaultResponse {
        return generateJWT(refreshData = refreshData)
    }

    private fun generateJWT(user: User = User(), refreshData: RefreshData = RefreshData()): DefaultResponse {
        val refresh = try {
            refreshRepository.findByRefresh(refreshData.refresh)
        } catch (e: EmptyResultDataAccessException) {
            Refresh()
        }

        val foundUser = if (user.uid.isBlank()) {
            val payload = try {
                String(
                    Base64.getDecoder().decode(
                        refresh.token.split(".")[1]
                    )
                )
            } catch (e: Throwable) {
                throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
            }
            try {
                userRepository.findByUid(JSONObject(payload)["user"].toString())
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("User not found", BAD_REQUEST)
            } catch (e: Throwable) {
                throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
            }
        } else user

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        try {
            if (refresh.refresh.isNotBlank() && refresh.exp.isNotBlank()) {
                if (dateFormatter.parse(refresh.exp).time - Date(System.currentTimeMillis() + 1000000).time < 0)
                    throw IllegalArgumentException()
            } else {
                refresh.exp = dateFormatter.format(Date(System.currentTimeMillis() + 36000000))
            }
        } catch (e: Throwable) {
            throw RequestException("Incorrect expiration date", BAD_REQUEST)
        }

        val roles = rolesFormatter(foundUser.roles.map {
            try {
                roleRepository.findByUid(it)
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("Role not found", FORBIDDEN)
            }
        })

        val token = try {
            JWT.create()
                .withClaim("permission", roles)
                .withClaim("user", foundUser.uid)
                .withClaim("iat", Date())
                .withExpiresAt(Date(System.currentTimeMillis() + 900000)).sign(
                    Algorithm.HMAC256("secret")
                )
        } catch (e: Throwable) {
            throw RequestException("Denied JWT create", BAD_REQUEST)
        }

        val userRefresh = try {
            refreshRepository.findByUser(foundUser.uid)
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("Refresh not found", BAD_REQUEST)
        }
        userRefresh.exp = refresh.exp
        userRefresh.token = token
        refreshRepository.save(userRefresh)

        return DefaultResponse(RefreshData(userRefresh.refresh, token))
    }

    private fun rolesFormatter(roles: List<Role>): Map<String, Int> {
        val rolesMap = mutableMapOf<String, Int>()
        PERMISSIONS.permissions.forEach {
            rolesMap[it.uname] = it.permissionLevel
        }
        roles.forEach { role ->
            role.permissions.forEach { permission ->
                if (rolesMap[permission.uname] != null) {
                    if (rolesMap[permission.uname]!! < permission.permissionLevel)
                        rolesMap[permission.uname] = permission.permissionLevel
                } else throw RequestException("Permission is missing", FORBIDDEN)
            }
        }
        return rolesMap
    }
}