package ru.cobalt42.auth.config.security

import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class JwtFilterConfiguration : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(JwtFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }
}