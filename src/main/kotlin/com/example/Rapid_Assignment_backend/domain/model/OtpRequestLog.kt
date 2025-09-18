package com.example.Rapid_Assignment_backend.domain.model

import com.example.Rapid_Assignment_backend.utils.EnumsRole
import com.example.Rapid_Assignment_backend.utils.EnumsType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import java.time.Instant
import java.time.temporal.ChronoUnit

data class OtpRequestLog(
    @Id val id : String? = null,
    val type : EnumsType,
    val role : EnumsRole,
    val email : String,
    val requestedAt : Instant = Instant.now(),

    @Indexed(name = "otp_request_expire_idx", expireAfter = "7d")
    val expiresAt : Instant = Instant.now().plus(7, ChronoUnit.DAYS)
)
