package com.example.Rapid_Assignment_backend.services.user

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.dto.user.UserProfileResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileUpdateRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
class UserProfileService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val bCryptEncoder: PasswordEncoder,
    private val cloudinary: Cloudinary
) {

    fun profile(): UserProfileResponse {
        val session = SessionContext.getSession()

        val user = userRepository.findById(session.userId).orElseThrow {
            NoSuchElementException("User not found")
        }

        return UserProfileResponse(
            email = user.email,
            name = user.name,
            rollNumber = user.rollNumber,
            branch = user.branch,
            profilePic = user.profilePic
        )
    }

    fun updateProfile(request: UserProfileUpdateRequest): UserProfileResponse {
        val session = SessionContext.getSession()

        val user = userRepository.findById(session.userId).orElseThrow {
            NoSuchElementException("User not found")
        }

        if (bCryptEncoder.matches(request.password, user.passwordHash).not()) {
            throw IllegalArgumentException("Incorrect password")
        }

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            rollNumber = request.rollNumber ?: user.rollNumber,
            branch = request.branch ?: user.branch,
            profilePic = request.profilePic ?: user.profilePic
        )

        userRepository.save(updatedUser)

        return UserProfileResponse(
            email = updatedUser.email,
            name = updatedUser.name,
            rollNumber = updatedUser.rollNumber,
            branch = updatedUser.branch,
            profilePic = updatedUser.profilePic
        )
    }


    fun uploadProfilePic(file: MultipartFile): UserProfileResponse {
        val session = SessionContext.getSession()

        val user = userRepository.findById(session.userId).orElseThrow {
            IllegalArgumentException("User not found")
        }

        val uploadResult = cloudinary.uploader().upload(file.bytes, mapOf("folder" to "RapidAssignment/users/profile_pics"))
        val imageUrl = uploadResult["secure_url"].toString()

        val updatedUser = user.copy(
            profilePic = imageUrl
        )

        userRepository.save(updatedUser)

        return UserProfileResponse(
            email = updatedUser.email,
            name = updatedUser.name,
            rollNumber = updatedUser.rollNumber,
            branch = updatedUser.branch,
            profilePic = updatedUser.profilePic
        )
    }
}