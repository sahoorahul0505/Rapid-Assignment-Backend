package com.example.Rapid_Assignment_backend.domain.model.user

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("sessions")
data class Session(
    @Id val id : ObjectId? = null,
    val token: String, // UUID
    val userId: ObjectId,
    val expiresAt: Instant
)
