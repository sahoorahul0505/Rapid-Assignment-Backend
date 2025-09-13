package com.example.Rapid_Assignment_backend.services

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.InvalidCredentialException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.dto.user.UserProfileResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileUpdateRequest
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
class UserProfileService(
    private val userRepository: UserRepository,
    private val bCryptEncoder: PasswordEncoder,
    private val cloudinary: Cloudinary
) {

    fun fetchUserProfile(): UserProfileResponse {
        val session = SessionContext.getSession()

        val user = userRepository.findById(session.userId).orElseThrow {
            NotFoundException("Account not found, Please login again.")
        }

        return UserProfileResponse(
            email = user.email,
            name = user.name,
            rollNumber = user.rollNumber,
            branch = user.branch,
            profilePic = user.profilePic
        )
    }

    fun updateProfile(request: UserProfileUpdateRequest, password : String) {
        val session = SessionContext.getSession()

        val user = userRepository.findById(session.userId).orElseThrow {
            NotFoundException("Account not found, Please login again.")
        }

        if (bCryptEncoder.matches(password, user.passwordHash).not()) {
            throw InvalidCredentialException()
        }

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            rollNumber = request.rollNumber ?: user.rollNumber,
            branch = request.branch ?: user.branch,
            profilePic = request.profilePic ?: user.profilePic
        )

        userRepository.save(updatedUser)
    }


    fun uploadProfilePic(file: MultipartFile){
        val session = SessionContext.getSession()
        if (session.expiresAt.isBefore(Instant.now())){
            throw NotFoundException("Session expired, Please Login again.")
        }
        val user = userRepository.findById(session.userId).orElseThrow {
            NotFoundException("Account not found, Please login again")
        }

        val uploadResult = cloudinary.uploader().upload(file.bytes, mapOf("folder" to "RapidAssignment/users/profile_pics"))
        val imageUrl = uploadResult["secure_url"].toString()

        val updatedUser = user.copy(
            profilePic = imageUrl
        )

        userRepository.save(updatedUser)

    }
}