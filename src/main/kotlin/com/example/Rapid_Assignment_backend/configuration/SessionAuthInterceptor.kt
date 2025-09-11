package com.example.Rapid_Assignment_backend.configuration

import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.Exception
import java.time.Instant

@Component
class SessionAuthInterceptor(
    private val sessionRepository: SessionRepository
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {

        // skip for endpoint start with /auth
        val path = request.servletPath
        if (path.startsWith("/auth/")){
            // skip auth endpoint
            return true
        }

        val token = request.getHeader("X-Session-token")
            ?: throw UnauthorizedException("Missing session token")
        val session = sessionRepository.findByToken(token)
            ?: throw UnauthorizedException("Invalid token, please login again to get access to this resource")
        if (session.expiresAt.isBefore(Instant.now())){
            throw UnauthorizedException("Session token expired, please login again")
        }

        // if valid token -> store in cotext
        SessionContext.setSession(session)
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        SessionContext.clearSession()
    }


//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ) {
//        try {
//            val token = request.getHeader("X-Session-Token")
//                ?: throw UnauthorizedException("Missing session token")
//
//            val session = sessionRepository.findByToken(token)
//                ?: throw UnauthorizedException("Invalid session token")
//
//            if (session.expiresAt.isBefore(Instant.now())){
//                throw UnauthorizedException("Session token expired, please login again")
//            }
//            // token valid -> store in context
//            SessionContext.setSession(session)
//
//            filterChain.doFilter(request, response)
//        } finally {
//            // Always clear ThreadLocal to avoid leaks
//            SessionContext.clearSession()
//        }
//    }
//
//    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
//        val path = request.servletPath
//        return path.startsWith("/auth/")
//    }

}