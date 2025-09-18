package com.example.Rapid_Assignment_backend.services

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.configuration.HashEncoder
import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.BadRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.dto.user.UserProfileResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileUpdateRequest
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserProfileService(
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val cloudinary: Cloudinary
) {

    fun fetchUserProfile(): UserProfileResponse {
        val session = SessionContext.getSession()

        val savedUser = userRepository.findById(session.userId).orElseThrow {
            NotFoundException("Account not found, Please login.")
        }

        return UserProfileResponse(
            email = savedUser.email,
            name = savedUser.name,
            rollNumber = savedUser.rollNumber,
            branch = savedUser.branch,
            profilePic = savedUser.profilePic
        )
    }

    fun updateUserProfile(request: UserProfileUpdateRequest) {
        val session = SessionContext.getSession()

        val savedUser = userRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found, Please login.")
        }

        if (hashEncoder.matches(request.password, savedUser.passwordHash).not()) {
            throw BadRequestException("Invalid Credential.")
        }

        val userToUpdate = savedUser.copy(
            name = request.name ?: savedUser.name,
            rollNumber = request.rollNumber ?: savedUser.rollNumber,
            branch = request.branch ?: savedUser.branch,
            profilePic = request.profilePic ?: savedUser.profilePic,
        )

        userRepository.save(userToUpdate)
    }


    fun uploadUserProfilePic(file: MultipartFile) {
        val session = SessionContext.getSession()
        val savedUser = userRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found, Please login.")
        }

        val publicId = "/${savedUser.role}_${session.userId}_profile"
        val uploadResult =
            cloudinary.uploader().upload(
                file.bytes,
                mapOf(
                    "public_id" to publicId,
                    "overwrite" to true,
                    "folder" to "RapidAssignment/users/profile_pics"
                )
            )
        val imageUrl = uploadResult["secure_url"].toString()

        val profilePicToUpload = savedUser.copy(profilePic = imageUrl, profilePicPublicId = publicId)

        userRepository.save(profilePicToUpload)
    }
}