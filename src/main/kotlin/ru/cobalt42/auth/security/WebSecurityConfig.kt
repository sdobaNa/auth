package ru.cobalt42.auth.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import ru.cobalt42.auth.exception.CustomAuthenticationEntryPoint
import ru.cobalt42.auth.util.SystemMessages

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val requestProvider: RequestProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
) : WebSecurityConfigurerAdapter() {

    @Value("\${actuator.admin.token}")
    lateinit var adminToken: String

    override fun configure(http: HttpSecurity) {

        // Disable CSRF (cross site request forgery)
        http.csrf().disable().exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeRequests()
            .antMatchers("api/auth/**").hasRole("VerifiedToken")
            .antMatchers("actuator/**").hasRole("ActuatorAdmin")
            .anyRequest().authenticated()

        // Apply JWT
        http.apply(JwtFilterConfigurer(requestProvider, adminToken))

        http.cors()

        http.httpBasic()
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/api/auth/generate", "/api/auth/refresh/**")
    }
}