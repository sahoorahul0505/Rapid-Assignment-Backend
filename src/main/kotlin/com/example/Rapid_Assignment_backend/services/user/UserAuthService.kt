package com.example.Rapid_Assignment_backend.services.user

import com.example.Rapid_Assignment_backend.domain.model.common.Session
import com.example.Rapid_Assignment_backend.domain.model.common.Otp
import com.example.Rapid_Assignment_backend.domain.model.user.User
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginResponse
import com.example.Rapid_Assignment_backend.dto.common.ForgotPasswordRequest
import com.example.Rapid_Assignment_backend.dto.common.MailRequest
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterRequest
import com.example.Rapid_Assignment_backend.dto.common.ResetPasswordRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.common.OtpRepository
import com.example.Rapid_Assignment_backend.repositories.user.UserRepository
import com.example.Rapid_Assignment_backend.services.common.SendEmailService
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

    fun sendOtpForRegistration(request: UserRegisterRequest): String {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("This email is already used.")
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

        emailService.sendMail(
            MailRequest(
                to = request.email,
                subject = "OTP for Registration",
                body = "Hello, ${request.name}.\n\nYour OTP for Registration is\n\n  $newOtp\n\nIt expires in 5 minutes."
            )
        )

        return "Otp sent to ${request.email}"
    }

    fun verifyOtpAndRegister(request: UserRegisterRequest, otp: String): String {
        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Otp may be used, please send again.")
        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("OTP expired, please send again.")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw IllegalArgumentException("Invalid OTP")
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
        return "User register successfully"
    }

    fun sendOtpForLogin(request: CommonLoginRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("No account found on this email.")
        otpRepository.deleteByEmail(request.email)
        val newOtp = (100000..999999).random().toString()
        val hashedOtp = bCryptEncoder.encode(newOtp)
        val otpEntity = Otp(
            type = "USER",
            email = request.email,
            otp = hashedOtp
        )
        otpRepository.save(otpEntity)
        emailService.sendMail(
            MailRequest(
                to = request.email,
                subject = "OTP for login.",
                body = "Hello, ${user.name}.\n\nYour OTP for login is\n\n  $newOtp\n\nThis expires in 5 minutes"
            )
        )
        return "OTP sent to ${request.email}"
    }


    fun verifyOtpAndLogin(request: CommonLoginRequest, otp: String): CommonLoginResponse {
        val user =
            userRepository.findByEmail(request.email)
                ?: throw NoSuchElementException("No account found with this email")

        if (bCryptEncoder.matches(request.password, user.passwordHash).not()) {
            throw IllegalArgumentException("Incorrect password")
        }

        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("OTP may be used, please send again.")
        if (otpEntity.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("OTP expired, please send again")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()) {
            throw IllegalArgumentException("Invalid OTP")
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
        return CommonLoginResponse(token = token)
    }


    // RESET PASSWORD //

    fun sendOtpForForgotPassword(request : ForgotPasswordRequest): String {
        val user =
            userRepository.findByEmail(request.email) ?: throw IllegalArgumentException("No account with this email")

        // check recent otp last 1 hour
        val oneHourEgo = Instant.now().minus(1, ChronoUnit.HOURS)
        val recentOtp = otpRepository.findAll()
            .filter { otps ->
                otps.type == "FORGOT_PASSWORD" && otps.createdAt.isAfter(oneHourEgo)
            }

        if (recentOtp.size >= 5) {
            throw IllegalArgumentException("Too many OTP requests. Please try again later")
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

        emailService.sendMail(
            MailRequest(
                to = user.email,
                subject = "OTP for password reset",
                body = "Hello ${user.name}.\n\nYour OTP for password reset is\n\n $newOtp\n\nit expires in 5 minutes."
            )
        )

        return "OTP sent to ${user.email}"
    }


    fun validateOtpAndResetPassword(request: ResetPasswordRequest, otp : String): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("No account found on this email.")
        val otpEntity = otpRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("OTP already used")
        if (otpEntity.expiresAt.isBefore(Instant.now())){
            throw IllegalArgumentException("OTP expired, please send again.")
        }
        if (bCryptEncoder.matches(otp, otpEntity.otp).not()){
            throw IllegalArgumentException("Invalid OTP.")
        }

        // update password
        val newHashedPassword = bCryptEncoder.encode(request.newPassword)
        val updatedUser = user.copy(
            passwordHash = newHashedPassword
        )
        userRepository.save(updatedUser)

        // delete otp after use
        otpRepository.deleteByEmail(request.email)

        return "Password reset successfully"
    }

}
















