package com.example.Rapid_Assignment_backend.configuration

import com.example.Rapid_Assignment_backend.services.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionAuthFilter(
    private val authService: AuthService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = response.getHeader("X-Session-Token")
        if (token != null) {
            val userId = authService.validateSession(token = token)

            if (userId != null) {
                request.setAttribute("userId", userId.toHexString())
            }
        }
        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/auth/**") || path.startsWith("/questions")
    }

}