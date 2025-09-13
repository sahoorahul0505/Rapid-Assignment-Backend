package com.example.Rapid_Assignment_backend.services

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.configuration.errorHandler.ConflictException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.InvalidCredentialException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.InvalidRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.domain.model.Otp
import com.example.Rapid_Assignment_backend.domain.model.Session
import com.example.Rapid_Assignment_backend.domain.model.Teacher
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.SessionResponse
import com.example.Rapid_Assignment_backend.dto.teacher.RegisterOtpRequest
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherRegisterRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.OtpRepository
import com.example.Rapid_Assignment_backend.repositories.TeacherRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class TeacherAuthService(
    private val sessionRepository: SessionRepository,
    private val teacherRepository: TeacherRepository,
    private val otpRepository: OtpRepository,
    private val bCryptEncoder: PasswordEncoder,
    private val emailService: SendEmailService,
    private val cloudinary: Cloudinary
) {

    // --------------------- REGISTRATION ------------------------------------//
    fun sendOtpForRegistrationTeacher(request: RegisterOtpRequest) {
        if (teacherRepository.findByEmail(request.email) != null) {
            throw ConflictException(request.email, "email")
        }
        // delete previous otp's on this account
        otpRepository.deleteByEmail(request.email)
        // generate otp
        val newOtp = (100000..999999).random().toString()
        val hashedOtp = bCryptEncoder.encode(newOtp)
        val otpEntity = Otp(
            type = "TEACHER",
            email = request.email,
            otp = hashedOtp
        )

        // save otp in db
        otpRepository.save(otpEntity)

        // send email
        val purpose = "Registration"
        emailService.sendOtpMailHtmlBody(
            to = request.email,
            userName = request.name,
            otp = newOtp,
            purpose = purpose
        )
    }

    fun verifyOtpAndRegisterTeacher(request: TeacherRegisterRequest, otp: String): SessionResponse {
        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw NotFoundException("Otp may be used, please send again")

        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw InvalidRequestException("OTP expired, please send again.")
        }

        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw InvalidRequestException("Invalid OTP")
        }

        // OTP is valid -> register teacher
        val hashedPassword = bCryptEncoder.encode(request.password)
        val teacherEntity = Teacher(
            name = request.name,
            email = request.email,
            passwordHash = hashedPassword,
            teacherCode = request.teacherCode
        )
        teacherRepository.save(teacherEntity)
        // delete otp after success register
        otpRepository.deleteByEmail(request.email)
        val token = UUID.randomUUID().toString()
        val session = Session(
            role = "TEACHER",
            token = token,
            userId = teacherEntity.id!!
        )
        sessionRepository.save(session)
        return SessionResponse(
            token = token
        )
    }

    // -------------------------- LOGIN ------------------------------------------//
    fun sendOtpForLogin(request: CommonLoginRequest) {

        // check user exist
        val teacher = teacherRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account fount on this ${request.email}.")

        if (bCryptEncoder.matches(request.password, teacher.passwordHash).not()) {
            throw InvalidCredentialException()
        }
        // delete old otp for this email
        otpRepository.deleteByEmail(request.email)
        // generate otp
        val newOtp = (100000..999999).random().toString()
        val hashedOtp = bCryptEncoder.encode(newOtp)
        val otpEntity = Otp(
            type = "TEACHER",
            email = request.email,
            otp = hashedOtp
        )
        otpRepository.save(otpEntity)

        // sent otp to email
        val purpose = "Login"
        emailService.sendOtpMailHtmlBody(
            to = request.email,
            userName = teacher.name,
            otp = newOtp,
            purpose = purpose
        )
    }

    fun verifyOtpAndLogin(request: CommonLoginRequest, otp: String): SessionResponse {
        val teacher = teacherRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found on this ${request.email}, Please check email again.")

        if (bCryptEncoder.matches(request.password, teacher.passwordHash).not()) {
            throw InvalidCredentialException()
        }

        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw InvalidRequestException("OTP may be used, please send again")

        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw InvalidRequestException("OTP expired, please send again.")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw InvalidRequestException("Invalid OTP")
        }

        // delete old session token
        sessionRepository.deleteByUserId(userId = teacher.id!!)

        val token = UUID.randomUUID().toString()
        val session = Session(
            role = teacher.role,
            token = token,
            userId = teacher.id
        )
        sessionRepository.save(session)
        // delete otp after login
        otpRepository.deleteByEmail(request.email)
        return SessionResponse(token = token)
    }


}