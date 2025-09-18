package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.HashEncoder
import com.example.Rapid_Assignment_backend.configuration.errorHandler.BadRequestException
import com.example.Rapid_Assignment_backend.domain.model.Otp
import com.example.Rapid_Assignment_backend.domain.model.OtpRequestLog
import com.example.Rapid_Assignment_backend.utils.EnumsRole
import com.example.Rapid_Assignment_backend.utils.EnumsType
import com.example.Rapid_Assignment_backend.repositories.OtpRepository
import com.example.Rapid_Assignment_backend.repositories.OtpRequestLogRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class OtpService(
    private val otpRepository: OtpRepository,
    private val otpRequestLogRepository: OtpRequestLogRepository,
    private val hashEncoder: HashEncoder,
    private val emailService: EmailService
) {

    /*
     * Generate and send OTP
     */
    fun generateAndSendOtp(
        email: String,
        name: String,
        type: EnumsType,
        role: EnumsRole,
        hours: Long,
        maxRequest: Int
    ) {
        // enforce rate limit
        checkRateLimit(email, type, role, hours, maxRequest)
        // delete old otp before requesting new onw
        otpRepository.deleteByTypeAndRoleAndEmail(type, role, email)
        // generate OTP
        val plainOtp = (100000..999999).random().toString()
        val hashedOtp = hashEncoder.encoder(plainOtp)
        // save otp in db
        val otpToSave = Otp(
            type = type,
            role = role,
            email = email,
            otp = hashedOtp
        )
        otpRepository.save(otpToSave)

        // save request log fir otp count tracking
        val logToSave = OtpRequestLog(
            type = type,
            role = role,
            email = email,
        )
        otpRequestLogRepository.save(logToSave)

        // send otp to mail
        val purpose = type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        emailService.sendOtpMail(
            to = email,
            userName = name,
            otp = plainOtp,
            purpose = purpose
        )
    }

    /*
     * Verify OTP
     */
    fun verifyOtp(email: String, rawOtp: String, type: EnumsType, role: EnumsRole) {
        val savedOtp = otpRepository.findByTypeAndRoleAndEmail(type, role, email)
            ?: throw BadRequestException("OTP maybe used or expired. request a new one.")

//        if (savedOtp.expiresAt.isBefore(Instant.now())) {
//            throw BadRequestException("OTP has expired.")
//        }
//        if (hashEncoder.matches(rawOtp, savedOtp.otp).not()) {
//            throw BadRequestException("Invalid OTP.")
//        }

        when {
            savedOtp.expiresAt.isBefore(Instant.now()) ->
                throw BadRequestException("OTP has expired.")

            hashEncoder.matches(rawOtp, savedOtp.otp).not() ->
                throw BadRequestException("Invalid OTP.")
        }

        // delete otp after successful verification
        otpRepository.deleteByTypeAndRoleAndEmail(type, role, email)
    }


    private fun checkRateLimit(email: String, type: EnumsType, role: EnumsRole, hours: Long, maxRequest: Int) {
        val afterAgo = Instant.now().minus(hours, ChronoUnit.HOURS)
        val count = otpRequestLogRepository.countByTypeAndRoleAndEmailAndRequestedAtAfter(
            type = type,
            role = role,
            email = email,
            requestedAt = afterAgo
        )
        if (count >= maxRequest) {
            throw BadRequestException("You can request only $maxRequest OTP(s) every $hours. Please try again later.")
        }
    }
}