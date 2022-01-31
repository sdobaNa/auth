package ru.cobalt42.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.dto.DefaultResponse
import ru.cobalt42.auth.dto.RefreshData
import ru.cobalt42.auth.exception.RequestException
import ru.cobalt42.auth.model.Refresh
import ru.cobalt42.auth.model.role.Role
import ru.cobalt42.auth.model.user.User
import ru.cobalt42.auth.repository.auth.RefreshRepository
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.util.enums.Permissions.PERMISSIONS
import ru.cobalt42.auth.util.enums.UserStatuses.ENABLED
import ru.cobalt42.auth.util.enums.UserStatuses.EXPIRED
import java.text.SimpleDateFormat
import java.util.*

@Service
class AuthorizationServiceImpl(
    private val userRepository: UserRepository,
    private val refreshRepository: RefreshRepository,
    private val roleRepository: RoleRepository,
) : AuthorizationService {

    @Value("\${token.refresh.time}")
    private lateinit var refreshTime: String

    @Value("\${token.access.time}")
    private lateinit var accessTime: String

    @Value("\${security.jwt.token.secret-key}")
    private lateinit var key: String

    override fun generate(authorization: Authorization, isAdminPanel: Boolean): DefaultResponse {
        try {
            val user = userRepository.findByLogin(authorization.login)
            if (user.subExpDate.isNotBlank() && SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(user.subExpDate) < Date()) {
                user.statusId = EXPIRED.status
                userRepository.save(user)
            }
            if (user.statusId != ENABLED.status) throw RequestException("User is disabled", BAD_REQUEST)
            if (BCryptPasswordEncoder().matches(authorization.password, user.password)) {
                return generateJWT(user, isAdminPanel = isAdminPanel)
            } else {
                throw RequestException("Wrong password", BAD_REQUEST)
            }
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("Wrong login", BAD_REQUEST)
        }
    }

    override fun refresh(refreshData: RefreshData): DefaultResponse {
        return generateJWT(
            refresh = try {
                refreshRepository.findByRefresh(refreshData.refresh)
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
            }
        )
    }

    private fun generateJWT(
        user: User = User(),
        refresh: Refresh = Refresh(),
        isAdminPanel: Boolean = false
    ): DefaultResponse {
        val refreshEntry = try {
            refreshRepository.findByRefresh(refresh.refresh)
        } catch (e: EmptyResultDataAccessException) {
            Refresh()
        }

        val foundUser = if (user.uid.isBlank()) {
            val payload = try {
                String(
                    Base64.getDecoder().decode(
                        refreshEntry.token.split(".")[1]
                    )
                )
            } catch (e: Throwable) {
                throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
            }
            try {
                userRepository.findByUid(JSONObject(payload)["user"].toString())
                    .also {
                        if (user.statusId != ENABLED.status) throw RequestException(
                            "User is disabled",
                            BAD_REQUEST
                        )
                    }
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("User not found", BAD_REQUEST)
            } catch (e: Throwable) {
                throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
            }
        } else user

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        try {
            if (refreshEntry.refresh.isNotBlank() && refreshEntry.exp.isNotBlank()) {
                if (dateFormatter.parse(refreshEntry.exp).time - Date(System.currentTimeMillis() + 1000000).time < 0)
                    throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
                else if (dateFormatter.parse(refreshEntry.exp).time - Date(System.currentTimeMillis() + 1000000).time >= 0) {
                    refreshEntry.refresh = UUID.randomUUID().toString()
                    refreshEntry.exp = dateFormatter.format(Date(System.currentTimeMillis() + refreshTime.toInt()))
                }
            } else {
                refreshEntry.refresh = UUID.randomUUID().toString()
                refreshEntry.exp = dateFormatter.format(Date(System.currentTimeMillis() + refreshTime.toInt()))
            }
        } catch (e: NumberFormatException) {
            throw RequestException("Invalid property token.refresh.time", BAD_REQUEST)
        } catch (e: Throwable) {
            throw RequestException("Incorrect expiration date", BAD_REQUEST)
        }
        val userRoles = foundUser.roles.map {
            try {
                roleRepository.findByUid(it)
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("Role not found", FORBIDDEN)
            }
        }

        if (isAdminPanel && userRoles.find { it.name == "admin" } == null)
            throw RequestException("Access denied", BAD_REQUEST)

        val roles = rolesFormatter(userRoles)

        val token = try {
            JWT.create()
                .withClaim("permission", roles)
                .withClaim("user", foundUser.uid)
                .withIssuedAt(Date())
                .withExpiresAt(Date(System.currentTimeMillis() + accessTime.toInt())).sign(
                    Algorithm.HMAC256(key)
                )
        } catch (e: NumberFormatException) {
            throw RequestException("Invalid property token.access.time", BAD_REQUEST)
        } catch (e: Throwable) {
            throw RequestException("Denied JWT create", BAD_REQUEST)
        }

        val userRefresh = try {
            refreshRepository.findByUser(foundUser.uid)
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("Refresh not found", BAD_REQUEST)
        }
        userRefresh.refresh = refreshEntry.refresh
        userRefresh.exp = refreshEntry.exp
        userRefresh.token = token
        refreshRepository.save(userRefresh)
        return DefaultResponse(
            RefreshData(
                userRefresh.refresh,
                token,
                user.uid,
                user.name,
                user.organization,
                user.position,
                user.avatar
            )
        )
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