package com.example.Rapid_Assignment_backend.services

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.configuration.HashEncoder
import com.example.Rapid_Assignment_backend.configuration.errorHandler.BadRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.ConflictException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.domain.model.Session
import com.example.Rapid_Assignment_backend.domain.model.Teacher
import com.example.Rapid_Assignment_backend.utils.EnumsRole
import com.example.Rapid_Assignment_backend.utils.EnumsType
import com.example.Rapid_Assignment_backend.dto.common.LoginOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.LoginRequest
import com.example.Rapid_Assignment_backend.dto.common.PasswordResetRequest
import com.example.Rapid_Assignment_backend.dto.common.RegisterOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.PaswrdResetOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.SessionResponse
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherRegisterRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.TeacherRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TeacherAuthService(
    private val sessionRepository: SessionRepository,
    private val teacherRepository: TeacherRepository,
    private val hashEncoder: HashEncoder,
    private val emailService: EmailService,
    private val otpService: OtpService,
    private val cloudinary: Cloudinary
) {

    // --------------------- REGISTRATION ------------------------------------//
    fun sendOtpForRegistrationTeacher(request: RegisterOtpRequest) {
        if (teacherRepository.findByEmail(request.email) != null) {
            throw ConflictException("Email already in use.")
        }
        // send otp to mail
        otpService.generateAndSendOtp(
            email = request.email,
            name = request.name,
            type = EnumsType.REGISTER,
            role = EnumsRole.USER,
            hours = 1,
            maxRequest = 3
        )
    }

    fun verifyOtpAndRegisterTeacher(request: TeacherRegisterRequest): SessionResponse {
        otpService.verifyOtp(
            email = request.email,
            rawOtp = request.otp,
            type = EnumsType.REGISTER,
            role = EnumsRole.TEACHER
        )

        // OTP is valid -> register teacher
        val hashedPassword = hashEncoder.encoder(request.password)
        val teacherToSave = Teacher(
            name = request.name,
            email = request.email,
            passwordHash = hashedPassword,
            teacherCode = request.teacherCode
        )
        val savedTeacher = teacherRepository.save(teacherToSave)

        val token = UUID.randomUUID().toString()
        val session = Session(
            role = EnumsRole.TEACHER,
            token = token,
            userId = savedTeacher.id!!
        )
        sessionRepository.save(session)
        return SessionResponse(
            token = token
        )
    }

    // -------------------------- LOGIN ------------------------------------------//
    fun sendOtpForLoginTeacher(request: LoginOtpRequest) {

        // check user exist
        val savedTeacher = teacherRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found for this email.")

        if (hashEncoder.matches(request.password, savedTeacher.passwordHash).not()) {
            throw BadRequestException("Invalid Credentials.")
        }
        otpService.generateAndSendOtp(
            email = request.email,
            name = savedTeacher.name,
            type = EnumsType.LOGIN,
            role = savedTeacher.role,
            hours = 1,
            maxRequest = 3
        )
    }

    fun verifyOtpAndLoginTeacher(request: LoginRequest): SessionResponse {
        val savedTeacher = teacherRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found for this email.")

        if (hashEncoder.matches(request.password, savedTeacher.passwordHash).not()) {
            throw BadRequestException("Invalid credentials.")
        }
        otpService.verifyOtp(
            email = request.email,
            rawOtp = request.otp,
            type = EnumsType.LOGIN,
            role = savedTeacher.role
        )
        // delete old session token
        sessionRepository.deleteByUserId(userId = savedTeacher.id!!)

        val token = UUID.randomUUID().toString()
        val session = Session(
            role = savedTeacher.role,
            token = token,
            userId = savedTeacher.id
        )
        sessionRepository.save(session)
        return SessionResponse(token = token)
    }

    // -------------------- RESET PASSWORD ---------------------------------- //
    fun sendOtpForResetPasswordTeacher(request: PaswrdResetOtpRequest) {
        val savedTeacher = teacherRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found for this email.")
        otpService.generateAndSendOtp(
            email = request.email,
            name = savedTeacher.name,
            type = EnumsType.FORGOT_PAASWORD,
            role = savedTeacher.role,
            hours = 24,
            maxRequest = 3,
        )
    }

    fun verifyOtpAndResetPassword(request: PasswordResetRequest) {
        val savedTeacher = teacherRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found for this email.")

        otpService.verifyOtp(
            email = request.email,
            rawOtp = request.otp,
            type = EnumsType.FORGOT_PAASWORD,
            role = savedTeacher.role,
        )
        val hashedPassword = hashEncoder.encoder(request.newPassword)
        val passwordToUpdate = savedTeacher.copy(
            passwordHash = hashedPassword
        )
        teacherRepository.save(passwordToUpdate)
        emailService.sendPasswordChangedMail(
            to = savedTeacher.email,
            userName = savedTeacher.name
        )
    }
}