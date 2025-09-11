package com.example.Rapid_Assignment_backend.domain.model.common

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.temporal.ChronoUnit

@Document("otp's")
data class Otp(
    @Id val id: ObjectId? = null,
    val type : String,
    val email: String,
    val otp: String,
    val createdAt: Instant = Instant.now(),
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant = Instant.now().plus(5, ChronoUnit.MINUTES)
)
