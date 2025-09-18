package com.example.Rapid_Assignment_backend.domain.model

import com.example.Rapid_Assignment_backend.utils.EnumsRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.temporal.ChronoUnit

@Document("sessions")
data class Session(
    @Id val id: String? = null,
    val role: EnumsRole,
    val token: String, // UUID
    val userId: String,
    @Indexed(name = "session_expire_idx", expireAfter = "10m")
    val expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS)
)