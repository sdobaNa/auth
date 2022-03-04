package ru.cobalt42.auth.exception

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    @Throws(IOException::class, ServletException::class, RequestException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
    }
}
