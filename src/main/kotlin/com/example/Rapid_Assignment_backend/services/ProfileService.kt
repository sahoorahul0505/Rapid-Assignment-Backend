package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.dto.ProfileResponse
import com.example.Rapid_Assignment_backend.dto.ProfileUpdateRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun profile(token: String): ProfileResponse {
        val session = sessionRepository.findByToken(token) ?: throw IllegalArgumentException("Invalid session token")

        if (session.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Session expired, please login again")
        }

        val user = userRepository.findById(session.userId).orElseThrow {
            NoSuchElementException("User not found")
        }

        return ProfileResponse(
            email = user.email,
            name = user.name,
            rollNumber = user.rollNumber,
            branch = user.branch
        )
    }

    fun updateProfile(token : String, request : ProfileUpdateRequest): ProfileResponse {
        val session = sessionRepository.findByToken(token) ?: throw IllegalArgumentException("Invalid session token")

        if (session.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Session expired, please login again")
        }

        val user = userRepository.findById(session.userId).orElseThrow {
            NoSuchElementException("User not found")
        }

        if (passwordEncoder.matches(request.password, user.passwordHash).not()){
            throw IllegalArgumentException("Incorrect password")
        }

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            rollNumber = request.rollNumber ?: user.rollNumber,
            branch = request.branch ?: user.branch
        )

        userRepository.save(updatedUser)

        return ProfileResponse(
            email = updatedUser.email,
            name = updatedUser.name,
            rollNumber = updatedUser.rollNumber,
            branch = updatedUser.branch
        )
    }
}