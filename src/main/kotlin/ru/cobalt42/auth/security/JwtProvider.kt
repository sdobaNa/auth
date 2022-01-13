package ru.cobalt42.auth.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import ru.cobalt42.auth.exception.RequestException
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtProvider {
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
}