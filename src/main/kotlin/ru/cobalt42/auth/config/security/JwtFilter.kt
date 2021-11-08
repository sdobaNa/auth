package ru.cobalt42.auth.config.security

import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import ru.cobalt42.auth.util.writeLog
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JwtFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(httpServletRequest)
        httpServletResponse.status = 200
        filterChain.doFilter(wrappedRequest, httpServletResponse)
        writeLog(wrappedRequest, httpServletResponse)
    }
}