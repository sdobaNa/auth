package ru.cobalt42.auth.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Component
import ru.cobalt42.auth.exception.RequestException
import ru.cobalt42.auth.repository.auth.RoleRepository
import ru.cobalt42.auth.repository.auth.UserRepository
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtProvider(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    @Value("\${security.jwt.token.secret-key}")
    private lateinit var secretKey: String

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else if (req.getParameter("jwt") != null && req.getParameter("jwt").isNotBlank()) {
            req.getParameter("jwt")
        } else null
    }

    fun validateToken(token: String?): Boolean {
        if (token.isNullOrBlank()) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        }
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        } catch (e: IllegalArgumentException) {
            throw RequestException("Error 500", INTERNAL_SERVER_ERROR)
        }
    }

    fun adminCheck(token: String?, httpServletRequest: HttpServletRequest) {
        val payload = try {
            String(
                Base64.getDecoder().decode(
                    token!!.split(".")[1]
                )
            )
        } catch (e: IllegalArgumentException) {
            throw RequestException("Expired or invalid JWT token", UNAUTHORIZED)
        }
        val userUid = try {
            JSONObject(payload)["userUid"].toString()
        } catch (e: Throwable) {
            throw RequestException("System error", INTERNAL_SERVER_ERROR)
        }

        val path = httpServletRequest.requestURI.split("/")
        try {
            when (httpServletRequest.method) {
                "GET", "POST" -> {
                    if (path.size == 5 && path[3] == "user")
                        return
                }
            }
        } catch (e: NullPointerException) {
            throw RequestException("Invalid path", INTERNAL_SERVER_ERROR)
        }

        if (try {
                userRepository.getByUid(userUid).roles.map { roleRepository.getByUid(it) }
                    .any { it.name == "admin" }
            } catch (e: EmptyResultDataAccessException) {
                throw RequestException("User or role is missing", UNAUTHORIZED)
            }
        ) return else throw RequestException("Permission denied", FORBIDDEN)
    }
}