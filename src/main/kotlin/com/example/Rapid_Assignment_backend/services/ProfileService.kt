package com.example.Rapid_Assignment_backend.services

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.dto.ProfileResponse
import com.example.Rapid_Assignment_backend.dto.ProfileUpdateRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val cloudinary: Cloudinary
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
            branch = user.branch,
            profilePic = user.profilePic
        )
    }

    fun updateProfile(token: String, request: ProfileUpdateRequest): ProfileResponse {
        val session = sessionRepository.findByToken(token) ?: throw IllegalArgumentException("Invalid session token")

        if (session.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Session expired, please login again")
        }

        val user = userRepository.findById(session.userId).orElseThrow {
            NoSuchElementException("User not found")
        }

        if (passwordEncoder.matches(request.password, user.passwordHash).not()) {
            throw IllegalArgumentException("Incorrect password")
        }

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            rollNumber = request.rollNumber ?: user.rollNumber,
            branch = request.branch ?: user.branch,
            profilePic = request.profilePic ?: user.profilePic
        )

        userRepository.save(updatedUser)

        return ProfileResponse(
            email = updatedUser.email,
            name = updatedUser.name,
            rollNumber = updatedUser.rollNumber,
            branch = updatedUser.branch,
            profilePic = updatedUser.profilePic
        )
    }


    fun uploadProfilePic(token: String, file: MultipartFile): ProfileResponse {
        val session = sessionRepository.findByToken(token) ?: throw IllegalArgumentException("Invalid session token")

        if (session.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Session token expired. Please login again.")
        }

        val user = userRepository.findById(session.userId).orElseThrow {
            IllegalArgumentException("User not found")
        }

        val uploadResult = cloudinary.uploader().upload(file.bytes, mapOf("folder" to "RapidAssignment/users/profile_pics"))
        val imageUrl = uploadResult["secure_url"].toString()

        val updatedUser = user.copy(
            profilePic = imageUrl
        )

        userRepository.save(updatedUser)

        return ProfileResponse(
            email = updatedUser.email,
            name = updatedUser.name,
            rollNumber = updatedUser.rollNumber,
            branch = updatedUser.branch,
            profilePic = updatedUser.profilePic
        )
    }
}