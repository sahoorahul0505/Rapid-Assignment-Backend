package com.example.Rapid_Assignment_backend.domain.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.temporal.ChronoUnit

@Document("sessions")
data class Session(
    @Id val id : ObjectId? = null,
    val role : String,
    val token: String, // UUID
    val userId: ObjectId,
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS)
)