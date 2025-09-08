package com.example.Rapid_Assignment_backend.domain.model.user

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.temporal.ChronoUnit


@Document("password_reset_otps")
data class PasswordResetOtp(
    @Id val id: ObjectId? = null,
    val userId: ObjectId,
    val otp: String,
    val expiresAt: Instant = Instant.now().plus(5, ChronoUnit.MINUTES),
    val createdAt: Instant = Instant.now()
)
