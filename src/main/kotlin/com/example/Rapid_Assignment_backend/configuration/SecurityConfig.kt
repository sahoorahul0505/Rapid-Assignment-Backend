package com.example.Rapid_Assignment_backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun bCrypEncoder() : BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http : HttpSecurity) : SecurityFilterChain {
        http.csrf { it.disable() } // disable CSRF for APIS
            .authorizeHttpRequests {
                it.requestMatchers("/**").permitAll() // Interceptor will handle the security
            }

        return http.build()
    }
}