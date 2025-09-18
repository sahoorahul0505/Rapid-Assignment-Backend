package com.example.Rapid_Assignment_backend.services

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.configuration.HashEncoder
import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.BadRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherProfileResponse
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherProfileUpdateRequest
import com.example.Rapid_Assignment_backend.repositories.TeacherRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class TeacherProfileService(
    private val teacherRepository: TeacherRepository,
    private val hashEncoder: HashEncoder,
    private val cloudinary: Cloudinary
) {

    fun fetchTeacherProfile(): TeacherProfileResponse {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("Account not found. Please login.")
        }

        return TeacherProfileResponse(
            email = savedTeacher.email,
            name = savedTeacher.name,
            teacherCode = savedTeacher.teacherCode,
            profilePic = savedTeacher.profilePic
        )
    }

    fun updateTeacherProfile(request: TeacherProfileUpdateRequest) {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found. Please login.")
        }

        if (hashEncoder.matches(request.password, savedTeacher.passwordHash).not()) {
            throw BadRequestException("Invalid credential.")
        }

        val teacherToUpdate = savedTeacher.copy(
            name = request.name ?: savedTeacher.name,
            teacherCode = request.teacherCode ?: savedTeacher.teacherCode,
            profilePic = request.profilePic ?: savedTeacher.profilePic
        )

        teacherRepository.save(teacherToUpdate)
    }

    fun uploadTeacherProfilePic(file: MultipartFile) {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found, Please login.")
        }
        val publicId = "/${savedTeacher.role}_${session.userId}_profile"
        val uploadResult =
            cloudinary.uploader().upload(
                file.bytes,
                mapOf(
                    "public_id" to publicId,
                    "overwrite" to true,
                    "folder" to "RapidAssignment/teachers/profile_pics"
                )
            )
        val imageUrl = uploadResult["secure_url"].toString()

        val profilePicToUpload = savedTeacher.copy(profilePic = imageUrl, profilePicPublicId = publicId)
        teacherRepository.save(profilePicToUpload)
    }
}