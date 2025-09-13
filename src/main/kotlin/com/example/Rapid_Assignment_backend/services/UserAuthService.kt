package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.errorHandler.ConflictException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.InvalidCredentialException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.InvalidRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.domain.model.Session
import com.example.Rapid_Assignment_backend.domain.model.Otp
import com.example.Rapid_Assignment_backend.domain.model.User
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.ForgotPasswordRequest
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.ResetPasswordRequest
import com.example.Rapid_Assignment_backend.dto.common.SessionResponse
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.OtpRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class UserAuthService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val bCryptEncoder: BCryptPasswordEncoder,
    private val otpRepository: OtpRepository,
    private val emailService: SendEmailService

) {

    fun sendOtpForRegistrationUser(request: UserRegisterOtpRequest) {
        if (userRepository.findByEmail(request.email) != null) {
            throw ConflictException(request.email, "email")
        }
        // delete previous otp's on this account
        otpRepository.deleteByEmail(request.email)
        // generate otp
        val newOtp = (100000..999999).random().toString()
        val hashedOtp = bCryptEncoder.encode(newOtp)
        val otpEntity = Otp(
            type = "USER",
            email = request.email,
            otp = hashedOtp
        )
        otpRepository.save(otpEntity)

        val purpose = "Registration"
        emailService.sendOtpMailHtmlBody(
            to = request.email,
            userName = request.name,
            otp = newOtp,
            purpose = purpose
        )
    }

    fun verifyOtpAndRegisterUser(request: UserRegisterRequest, otp: String): SessionResponse {
        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw NotFoundException("OTP may be used, please send again.")
        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw InvalidRequestException("OTP expired, please send again.")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw InvalidRequestException("Invalid OTP")
        }

        // OTP is valid -> register user
        val hashedPassword = bCryptEncoder.encode(request.password)
        val userEntity = User(
            email = request.email,
            passwordHash = hashedPassword,
            name = request.name,
            rollNumber = request.rollNumber,
            branch = request.branch
        )
        userRepository.save(userEntity)
        otpRepository.deleteByEmail(request.email)
        val token = UUID.randomUUID().toString()
        val session = Session(
            role = "USER",
            token = token,
            userId = userEntity.id!!
        )
        sessionRepository.save(session)
        return SessionResponse(
            token = token
        )
    }

    fun sendOtpForLogin(request: CommonLoginRequest) {
        val user = userRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account fount on this ${request.email}.")
        if (bCryptEncoder.matches(request.password, user.passwordHash).not()) {
            throw InvalidCredentialException()
        }
        otpRepository.deleteByEmail(request.email)
        val newOtp = (100000..999999).random().toString()
        val hashedOtp = bCryptEncoder.encode(newOtp)
        val otpEntity = Otp(
            type = "USER",
            email = request.email,
            otp = hashedOtp
        )
        otpRepository.save(otpEntity)
        val purpose = "Login"
        emailService.sendOtpMailHtmlBody(
            to = request.email,
            userName = user.name,
            otp = newOtp,
            purpose = purpose
        )
    }


    fun verifyOtpAndLogin(request: CommonLoginRequest, otp: String): SessionResponse {
        val user =
            userRepository.findByEmail(request.email)
                ?: throw NotFoundException("No account found on this ${request.email}, Please check email again.")

        if (bCryptEncoder.matches(request.password, user.passwordHash).not()) {
            throw InvalidCredentialException()
        }

        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw InvalidRequestException("OTP may be used, Please send again.")
        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw InvalidRequestException("OTP expired, please send again")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw InvalidRequestException("Invalid OTP")
        }
        sessionRepository.deleteByUserId(userId = user.id!!)

        val token = UUID.randomUUID().toString()
        val session = Session(
            role = user.role,
            token = token,
            userId = user.id
        )
        sessionRepository.save(session)
        otpRepository.deleteByEmail(request.email)
        return SessionResponse(token = token)
    }


    // RESET PASSWORD //

    fun sendOtpForForgotPassword(request: ForgotPasswordRequest) {
        val user =
            userRepository.findByEmail(request.email)
                ?: throw NotFoundException("No account found on this email, Please check email again.")

        // check recent otp last 1 hour
        val oneHourEgo = Instant.now().minus(1, ChronoUnit.HOURS)
        val recentOtp = otpRepository.findAll()
            .filter { otps ->
                otps.type == "FORGOT_PASSWORD" && otps.createdAt.isAfter(oneHourEgo)
            }

        if (recentOtp.size >= 5) {
            throw InvalidRequestException("Too many OTP requests. Please try again later")
        }
        val newOtp = (100000..999999).random().toString()
        val hashedOtp = bCryptEncoder.encode(newOtp)
        otpRepository.deleteByEmail(request.email)
        val otpEntity = Otp(
            type = "FORGOT_PASSWORD",
            email = request.email,
            otp = hashedOtp
        )
        otpRepository.save(otpEntity)

        val purpose = "Forgot Password"
        emailService.sendOtpMailHtmlBody(
            to = request.email,
            userName = user.name,
            otp = newOtp,
            purpose = purpose
        )
    }


    fun validateOtpAndResetPassword(request: ResetPasswordRequest, otp: String) {
        val user = userRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found on this email., Please check email again.")
        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw InvalidRequestException("OTP already used")
        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw InvalidRequestException("OTP expired, please send again.")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw InvalidRequestException("Invalid OTP.")
        }

        // update password
        val newHashedPassword = bCryptEncoder.encode(request.newPassword)
        val updatedUser = user.copy(
            passwordHash = newHashedPassword
        )
        userRepository.save(updatedUser)

        // delete otp after use
        otpRepository.deleteByEmail(request.email)

        emailService.sendPasswordMailHtmlBody(
            to = request.email,
            userName = user.name,
            password = request.newPassword
        )
    }

}
















