package ru.cobalt42.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.json.JSONException
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
import ru.cobalt42.auth.model.auth.Refresh
import ru.cobalt42.auth.model.auth.role.Role
import ru.cobalt42.auth.model.auth.user.User
import ru.cobalt42.auth.repository.auth.RefreshRepository
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import ru.cobalt42.auth.repository.common.ProjectRepository
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
    private val projectRepository: ProjectRepository,
) : AuthorizationService {

    @Value("\${token.refresh.time}")
    private lateinit var refreshTime: String

    @Value("\${token.access.time}")
    private lateinit var accessTime: String

    @Value("\${security.jwt.token.secret-key}")
    private lateinit var key: String

    override fun generate(authorization: Authorization, isAdminPanel: Boolean): DefaultResponse<RefreshData> {
        try {
            val user = userRepository.getByLogin(authorization.login)
            if (user.subExpDate.isNotBlank() && SimpleDateFormat("yyyy-MM-dd").parse(user.subExpDate) < Date()) {
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

    override fun refresh(authToken: String): DefaultResponse<RefreshData> {
        return generateJWT(authToken = authToken)
    }

    override fun changeProject(projectUid: String, authToken: String): DefaultResponse<RefreshData> {
        val user = try {
            userRepository.getByUid(getTokenParameter(authToken, "userUid"))
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("User not found", UNAUTHORIZED)
        }
        val project = try {
            projectRepository.getByUid(projectUid)
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("Project not found", UNAUTHORIZED)
        }
        if (project.objectInfo.groupUid == user.groupUid) {
            user.projectUid = projectUid
            userRepository.save(user)
            return generateJWT(user)
        } else throw RequestException("User group does not match", FORBIDDEN)
    }

    private fun generateJWT(
        user: User = User(),
        authToken: String = "",
        isAdminPanel: Boolean = false,
    ): DefaultResponse<RefreshData> {
        val refreshEntry = try {
            if (authToken.isNotBlank()) {
                refreshRepository.getByToken(authToken.split(" ")[1])
            } else throw EmptyResultDataAccessException(0)
        } catch (e: EmptyResultDataAccessException) {
            Refresh()
        } catch (e: Throwable) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        }

        val foundUser = if (user.uid.isBlank()) {
            try {
                userRepository.getByUid(getTokenParameter(refreshEntry.token, "userUid"))
                    .also {
                        if (it.statusId != ENABLED.status) throw RequestException(
                            "User is disabled",
                            BAD_REQUEST
                        )
                    }
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("User not found", BAD_REQUEST)
            } catch (e: JSONException) {
                throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
            }
        } else user

        val refreshToken = try {
            JWT.create()
                .withClaim("userUid", foundUser.uid)
                .withIssuedAt(Date())
                .withExpiresAt(Date(System.currentTimeMillis() + refreshTime.toInt())).sign(
                    Algorithm.HMAC256(key)
                )
        } catch (e: NumberFormatException) {
            throw RequestException("Invalid property token.access.time", BAD_REQUEST)
        } catch (e: Throwable) {
            throw RequestException("Denied JWT create", BAD_REQUEST)
        }
        if (refreshEntry.token.isNotBlank()) {
            try {
                if (JWT.decode(authToken.split(" ")[1]).expiresAt.time < Date(System.currentTimeMillis()).time)
                    throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
                else {
                    refreshEntry.token = refreshToken
                }
            } catch (e: NumberFormatException) {
                throw RequestException("Invalid property token.refresh.time", BAD_REQUEST)
            } catch (e: Throwable) {
                throw RequestException("Incorrect expiration date", UNAUTHORIZED)
            }
        } else {
            refreshEntry.token = refreshToken
        }
        val userRoles = foundUser.roles.map {
            try {
                roleRepository.getByUid(it)
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
                .withClaim("userUid", foundUser.uid)
                .withClaim("projectUid", foundUser.projectUid)
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
            refreshRepository.getByUserUid(foundUser.uid)
        } catch (e: EmptyResultDataAccessException) {
            throw RequestException("Refresh not found", BAD_REQUEST)
        }
        userRefresh.token = refreshEntry.token
        refreshRepository.save(userRefresh)
        return DefaultResponse(
            RefreshData(
                userRefresh.token,
                token,
                user.uid,
                user.name,
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

    private fun getTokenParameter(authToken: String, parameter: String): String {
        val payload = try {
            String(
                Base64.getDecoder().decode(
                    authToken.split(".")[1]
                )
            )
        } catch (e: IllegalArgumentException) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        } catch (e: IndexOutOfBoundsException) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        }
        return try {
            JSONObject(payload)[parameter].toString()
        } catch (e: Throwable) {
            throw RequestException("Token parameter is missing", UNAUTHORIZED)
        }
    }
}