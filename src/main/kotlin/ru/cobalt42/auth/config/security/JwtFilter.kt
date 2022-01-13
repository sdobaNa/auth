package ru.cobalt42.auth.config.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import ru.cobalt42.auth.exception.RequestException
import ru.cobalt42.auth.util.writeLog
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(
    private var jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(httpServletRequest)
        try {
            val token: String? = jwtProvider.resolveToken(httpServletRequest)
            if (jwtProvider.validateToken(token)) {
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    null, null, Collections.singletonList(
                        SimpleGrantedAuthority("VerifiedToken")
                    )
                )
            }
        } catch (ex: RequestException) {
            SecurityContextHolder.clearContext()
            httpServletResponse.sendError(ex.getHttpStatus().value(), ex.message)
            writeLog(wrappedRequest, httpServletResponse, jwtProvider, ex.message)
            return
        }
        filterChain.doFilter(wrappedRequest, httpServletResponse)
        writeLog(wrappedRequest, httpServletResponse, jwtProvider)
    }
}