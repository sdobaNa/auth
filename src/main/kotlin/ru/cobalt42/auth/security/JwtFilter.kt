package ru.cobalt42.auth.security

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import ru.cobalt42.auth.util.writeLog
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(
    private val adminToken: String,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain,
    ) {

        val wrappedRequest = ContentCachingRequestWrapper(httpServletRequest)
        if (httpServletRequest.requestURI.startsWith("/actuator"))
            if (httpServletRequest.getHeader(AUTHORIZATION) == "Bearer $adminToken")
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    null, null, Collections.singletonList(
                        SimpleGrantedAuthority("ActuatorAdmin")
                    )
                )
            else {
                writeLog(wrappedRequest, httpServletResponse, UNAUTHORIZED.reasonPhrase)
                httpServletResponse.sendError(UNAUTHORIZED.value(), UNAUTHORIZED.reasonPhrase)
                return
            }

        filterChain.doFilter(wrappedRequest, httpServletResponse)
    }

}