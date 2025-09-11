package com.example.Rapid_Assignment_backend.services.teacher

import com.cloudinary.Cloudinary
import com.example.Rapid_Assignment_backend.domain.model.common.Otp
import com.example.Rapid_Assignment_backend.domain.model.common.Session
import com.example.Rapid_Assignment_backend.domain.model.teacher.Teacher
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginResponse
import com.example.Rapid_Assignment_backend.dto.common.MailRequest
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherRegisterRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.common.OtpRepository
import com.example.Rapid_Assignment_backend.repositories.teacher.TeacherRepository
import com.example.Rapid_Assignment_backend.services.common.SendEmailService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class TeacherAuthService(
    private val sessionRepository: SessionRepository,
    private val teacherRepository: TeacherRepository,
    private val otpRepository: OtpRepository,
    private val commonLoginOtpRepository: OtpRepository,
    private val bCryptEncoder: PasswordEncoder,
    private val emailService: SendEmailService,
    private val cloudinary: Cloudinary
) {

    // --------------------- REGISTRATION ------------------------------------//
    fun sendOtpForRegistration(request: TeacherRegisterRequest): String {
        if (teacherRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("This email is already used by someone")
        }

        // generate otp
        val otp = (100000..999999).random().toString()

        // delete old otp for this email
        otpRepository.deleteByEmail(request.email)
        val hashedOtp = bCryptEncoder.encode(otp)
        val otpEntity = Otp(
            type = "TEACHER",
            email = request.email,
            otp = hashedOtp
        )

        // save otp in db
        otpRepository.save(otpEntity)

        // send email
        emailService.sendMail(
            MailRequest(
                to = request.email,
                subject = "OTP for Teacher Registration",
                body = "Hello ${request.name},\n\nYour OTP for Registration is\n\n    $otp\n\nIt expires in 5 minutes."
            )
        )

        return "Otp sent to ${request.email}"
    }

    fun verifyOtpAndRegister(request: TeacherRegisterRequest, otp: String): String {
        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Otp may be used, please send again")

        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("OTP expired, please send again.")
        }

        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw IllegalArgumentException("Invalid OTP")
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
        return "Teacher registered successfully"
    }

    // -------------------------- LOGIN ------------------------------------------//
    fun sendOtpForLogin(request: CommonLoginRequest): String {

        // check user exist
        val teacher = teacherRepository.findByEmail(request.email)
            ?: throw NoSuchElementException("No account found on this email")

        if (bCryptEncoder.matches(request.password, teacher.passwordHash).not()) {
            throw IllegalArgumentException("Incorrect password")
        }

        // delete old otp for this email
        commonLoginOtpRepository.deleteByEmail(request.email)

        // generate otp
        val otp = (100000..999999).random().toString()

        val hashedOtp = bCryptEncoder.encode(otp)
        val otpEntity = Otp(
            type = "TEACHER",
            email = request.email,
            otp = hashedOtp
        )
        commonLoginOtpRepository.save(otpEntity)

        // sent otp to email
        emailService.sendMail(
            MailRequest(
                to = request.email,
                subject = "OTP for Login",
                body = "Hello ${teacher.name} \n\n Your OTP for login is\n\n    $otp \n\nIt expires in 5 minutes"
            )
        )

        return "Otp sent to ${request.email}"
    }

    fun verifyOtpAndLogin(request: CommonLoginRequest, otp: String): CommonLoginResponse {
        val teacher = teacherRepository.findByEmail(request.email)
            ?: throw NoSuchElementException("No account found on this email")

        if (bCryptEncoder.matches(request.password, teacher.passwordHash).not()) {
            throw IllegalArgumentException("Incorrect password")
        }

        val otpEntity = commonLoginOtpRepository.findByEmail(request.email)
            ?: throw NoSuchElementException("Otp may be used, please send again")

        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("OTP expired, please send again.")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw IllegalArgumentException("Invalid OTP")
        }

        // delete old session token
        sessionRepository.deleteByUserId(teacher.id!!)

        val token = UUID.randomUUID().toString()
        val session = Session(
            role = teacher.role,
            token = token,
            userId = teacher.id
        )
        sessionRepository.save(session)

        // delete otp after login
        commonLoginOtpRepository.deleteByEmail(request.email)

        return CommonLoginResponse(token = token)
    }


}