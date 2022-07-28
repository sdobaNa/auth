package ru.cobalt42.auth.security

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import ru.cobalt42.auth.exception.BadRequestException
import ru.cobalt42.auth.exception.RequestException
import ru.cobalt42.auth.util.SystemMessages
import ru.cobalt42.auth.util.writeLog
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtFilter(
    private var requestProvider: RequestProvider,
    private val adminToken: String,
) : OncePerRequestFilter() {

    // 30 полей * 400 строк * 2 (на всякий) * 2 (размер одного символа) - 39 (начальное значение)
    private val requestLengthSize: Int = 47_961

    override fun doFilterInternal(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(httpServletRequest)
        if (httpServletRequest.requestURI.startsWith("/actuator"))
            if (httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION) == "Bearer $adminToken")
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    null, null, Collections.singletonList(
                        SimpleGrantedAuthority("ActuatorAdmin")
                    )
                )
            else {
                writeLog(wrappedRequest, httpServletResponse, requestProvider, HttpStatus.UNAUTHORIZED.reasonPhrase)
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
                return
            }
        else
            try {
                val token: String? = requestProvider.resolveToken(httpServletRequest)
                if (requestProvider.validateToken(token)) {
                    requestProvider.adminCheck(token, httpServletRequest)
                    SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                        null, null, Collections.singletonList(
                            SimpleGrantedAuthority("VerifiedToken")
                        )
                    )

                    requestProvider.validateRequestSize(httpServletRequest, token!!)
                }
            } catch (ex: RequestException) {
                SecurityContextHolder.clearContext()
                httpServletResponse.sendError(ex.getHttpStatus().value(), ex.message)
                writeLog(wrappedRequest, httpServletResponse, requestProvider, ex.message)
                return
            }
        filterChain.doFilter(wrappedRequest, httpServletResponse)
        writeLog(wrappedRequest, httpServletResponse, requestProvider)
    }

}