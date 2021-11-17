package ru.cobalt42.auth.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Value("\${actuator.admin.token}")
    lateinit var adminToken: String

    override fun configure(http: HttpSecurity) {

        // Disable CSRF (cross site request forgery)
        http.csrf().disable()

        // No session will be created or used by spring security
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // Entry points
        http.authorizeRequests()
            .antMatchers("actuator/**").hasRole("ActuatorAdmin")
            .anyRequest().permitAll()

        // Apply JWT
        http.apply(JwtFilterConfigurer(adminToken))

        http.cors()

        http.httpBasic()
    }

}