package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.HashEncoder
import com.example.Rapid_Assignment_backend.configuration.errorHandler.BadRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.ConflictException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.domain.model.Session
import com.example.Rapid_Assignment_backend.domain.model.User
import com.example.Rapid_Assignment_backend.utils.EnumsRole
import com.example.Rapid_Assignment_backend.utils.EnumsType
import com.example.Rapid_Assignment_backend.dto.common.*
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAuthService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val hashEncoder: HashEncoder,
    private val emailService: EmailService,
    private val otpService: OtpService

) {

    fun sendOtpForRegistrationUser(request: RegisterOtpRequest) {
        if (userRepository.findByEmail(request.email) != null) {
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

    fun verifyOtpAndRegisterUser(request: UserRegisterRequest): SessionResponse {
        // verify otp
        otpService.verifyOtp(
            email = request.email,
            rawOtp = request.otp,
            type = EnumsType.REGISTER,
            role = EnumsRole.USER
        )

        val hashedPassword = hashEncoder.encoder(request.password)
        val userToSave = User(
            email = request.email,
            passwordHash = hashedPassword,
            name = request.name,
            rollNumber = request.rollNumber,
            branch = request.branch,
        )
        val savedUser = userRepository.save(userToSave)
        val token = UUID.randomUUID().toString()
        val sessionToSave = Session(
            role = EnumsRole.USER,
            token = token,
            userId = savedUser.id!!
        )
        sessionRepository.save(sessionToSave)
        return SessionResponse(
            token = token
        )
    }

    fun sendOtpForLogin(request: LoginOtpRequest) {
        val savedUser = userRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found for this email.")
        if (hashEncoder.matches(request.password, savedUser.passwordHash).not()) {
            throw BadRequestException("Invalid credentials.")
        }
        otpService.generateAndSendOtp(
            email = request.email,
            name = savedUser.name,
            type = EnumsType.LOGIN,
            role = EnumsRole.USER,
            hours = 1,
            maxRequest = 3
        )
    }


    fun verifyOtpAndLogin(request: LoginRequest): SessionResponse {
        val savedUser =
            userRepository.findByEmail(request.email)
                ?: throw NotFoundException("No account found for this email.")

        if (hashEncoder.matches(request.password, savedUser.passwordHash).not()) {
            throw BadRequestException("Invalid credentials.")
        }

        otpService.verifyOtp(
            email = request.email,
            rawOtp = request.otp,
            type = EnumsType.LOGIN,
            role = savedUser.role
        )

        sessionRepository.deleteByUserId(userId = savedUser.id!!)

        val token = UUID.randomUUID().toString()
        val session = Session(
            role = savedUser.role,
            token = token,
            userId = savedUser.id
        )
        sessionRepository.save(session)
        return SessionResponse(token = token)
    }


    // RESET PASSWORD //

    fun sendOtpForResetPassword(request: PaswrdResetOtpRequest) {
        val savedUser =
            userRepository.findByEmail(request.email)
                ?: throw NotFoundException("No account found for this Email.")

        otpService.generateAndSendOtp(
            email = request.email,
            name = savedUser.name,
            type = EnumsType.FORGOT_PAASWORD,
            role = savedUser.role,
            hours = 24,
            maxRequest = 3
        )
    }


    fun validateOtpAndResetPassword(request: PasswordResetRequest) {
        val savedUser = userRepository.findByEmail(request.email)
            ?: throw NotFoundException("No account found for this email.")
        otpService.verifyOtp(
            email = request.email,
            rawOtp = request.otp,
            type = EnumsType.FORGOT_PAASWORD,
            role = savedUser.role
        )

        // update password
        val newHashedPassword = hashEncoder.encoder(request.newPassword)
        val userToUpdate = savedUser.copy(
            passwordHash = newHashedPassword,
        )
        userRepository.save(userToUpdate)

        emailService.sendPasswordChangedMail(
            to = savedUser.email,
            userName = savedUser.name
        )
    }
}