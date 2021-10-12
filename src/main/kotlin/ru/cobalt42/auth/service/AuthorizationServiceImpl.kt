package ru.cobalt42.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.json.JSONObject
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.cobalt42.auth.dto.Authorization
import ru.cobalt42.auth.dto.RefreshData
import ru.cobalt42.auth.model.Refresh
import ru.cobalt42.auth.model.User
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
    override fun generate(authorization: Authorization): RefreshData {
        try {
            val user = userRepository.findByLogin(authorization.login)
            if (BCryptPasswordEncoder().matches(authorization.password, user.password)) {
                return generateJWT(user)
            } else {
                throw Throwable("Wrong password")
            }
        } catch (e: EmptyResultDataAccessException) {
            throw Throwable("Wrong login")
        }
    }

    override fun refresh(refreshData: RefreshData): RefreshData {
        return generateJWT(refreshData = refreshData)
    }

    private fun generateJWT(user: User = User(), refreshData: RefreshData = RefreshData()): RefreshData {
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
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Expired or invalid JWT token")
            }
            try {
                userRepository.findByUid(JSONObject(payload)["user"].toString())
            } catch (e: EmptyResultDataAccessException) {
                throw Throwable("User not found")
            } catch (e: Throwable) {
                throw IllegalArgumentException("Expired or invalid JWT token")
            }
        } else user

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        try {
            if (refresh.refresh.isNotBlank() && refresh.exp.isNotBlank()) {
                if (dateFormatter.parse(refresh.exp).time - Date(System.currentTimeMillis() + 1000000).time < 0) {
                    return refreshData
                } else if (dateFormatter.parse(refresh.exp).time - Date(System.currentTimeMillis() + 1000000).time >= 0) {
                    refresh.exp = dateFormatter.format(Date(System.currentTimeMillis() + 86400000))
                }
            } else {
                refresh.exp = dateFormatter.format(Date(System.currentTimeMillis() + 86400000))
            }
        } catch (e: Throwable) {
            throw Throwable("Incorrect expiration date")
        }

        val roles = rolesFormatter(foundUser.roles.map {
            try {
                roleRepository.findByUid(it)
            } catch (e: EmptyResultDataAccessException) {
                throw ArrayIndexOutOfBoundsException("Role not found")
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
            throw Throwable("Denied JWT create")
        }

        val userRefresh = try {
            refreshRepository.findByUser(user.uid)
        } catch (e: EmptyResultDataAccessException) {
            throw Throwable("Refresh not found")
        }
        userRefresh.exp = refresh.exp
        userRefresh.token = token
        refreshRepository.save(userRefresh)

        return RefreshData(userRefresh.refresh, token)
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
                } else throw ArrayIndexOutOfBoundsException("Permission is missing")
            }
        }
        return rolesMap
    }
}