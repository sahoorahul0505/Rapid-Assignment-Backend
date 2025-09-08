package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.domain.model.user.PasswordResetOtp
import com.example.Rapid_Assignment_backend.domain.model.user.Session
import com.example.Rapid_Assignment_backend.domain.model.user.User
import com.example.Rapid_Assignment_backend.dto.LoginRequest
import com.example.Rapid_Assignment_backend.dto.LoginResponse
import com.example.Rapid_Assignment_backend.dto.MailRequest
import com.example.Rapid_Assignment_backend.dto.RegisterRequest
import com.example.Rapid_Assignment_backend.dto.ResetPasswordRequest
import com.example.Rapid_Assignment_backend.repositories.PasswordResetOtpRepository
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.bson.types.ObjectId
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val passwordResetOtpRepository: PasswordResetOtpRepository,
    private val emailService: SendEmailService

) {

    fun register(request: RegisterRequest): String {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("This email is already exist in database")
        }

        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            name = request.name,
            rollNumber = request.rollNumber,
            branch = request.branch
        )

        userRepository.save(user)

        return "User register successfully"
    }

    fun login(request: LoginRequest): LoginResponse {
        val user =
            userRepository.findByEmail(request.email)
                ?: throw IllegalArgumentException("Email does not exist in database")

        if (passwordEncoder.matches(request.password, user.passwordHash).not()) {
            throw IllegalArgumentException("Incorrect password")
        }

        sessionRepository.deleteByUserId(userId = user.id!!)

        val token = UUID.randomUUID().toString()
        val session = Session(
            token = token,
            userId = user.id,
            expiresAt = Instant.now().plus(7, ChronoUnit.DAYS)
        )
        sessionRepository.save(session)

        return LoginResponse(token = token)
    }

    fun logOut(token: String) {
        val session = sessionRepository.findByToken(token) ?: throw IllegalArgumentException("Invalid session token")
        if (session.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("Session token expired, please login again")
        } else {
            sessionRepository.deleteById(session.id!!.toHexString())
        }
    }

    fun validateSession(token: String): ObjectId? {
        val session = sessionRepository.findById(token).orElse(null) ?: return null
        if (session.expiresAt.isBefore(Instant.now())) {
            sessionRepository.deleteById(token)
            return null
        }
        return session.userId
    }


    // RESET PASSWORD //

    fun sendPasswordResetOtp(email: String): String {
        val user =
            userRepository.findByEmail(email) ?: throw IllegalArgumentException("No account with this email")
        // check recent otp last 1 hour
        val oneHourEgo = Instant.now().minus(1, ChronoUnit.HOURS)
        val recentOtps = passwordResetOtpRepository.findAll()
            .filter {
                it.userId == user.id && it.createdAt.isAfter(oneHourEgo)
            }

        if (recentOtps.size >= 3) {
            throw IllegalArgumentException("Too many OTP requests. Please try again later")
        }

        val otp = (100000..999999).random().toString()

        passwordResetOtpRepository.deleteByUserId(userId = user.id!!)

        val resetOtp = PasswordResetOtp(
            userId = user.id,
            otp = otp
        )
        passwordResetOtpRepository.save(resetOtp)

        emailService.sendMail(
            MailRequest(
                to = user.email,
                subject = "OTP for resset password",
                body = "Rapid Assignment \n Your OTP is \n $otp \n it will expire in 5 minutes."
            )
        )

        return "OTP sent to ${user.email}"
    }


    fun resetPasswordWithOtp(request: ResetPasswordRequest): String {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("No account with this email.")

        val resetOtp = passwordResetOtpRepository.findByUserId(userId = user.id!!)
            ?: throw IllegalArgumentException("No OPT found for this account.")

        if (resetOtp.expiresAt.isBefore(Instant.now())) {
            throw IllegalArgumentException("OTP expired")
        }

        if (resetOtp.otp != request.otp) {
            throw IllegalArgumentException("Invalid OTP")
        }

        // update password
        val newHashedPassword = passwordEncoder.encode(request.newPassword)
        val updatedUser = user.copy(
            passwordHash = newHashedPassword
        )
        userRepository.save(updatedUser)

        // delete otp after use
        passwordResetOtpRepository.deleteByUserId(userId = user.id)

        return "Password reset successfully"
    }

}
















