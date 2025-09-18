package com.example.Rapid_Assignment_backend.domain.model

import com.example.Rapid_Assignment_backend.utils.EnumsRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("users")
data class User(
    @Id val id: String? = null,
    val role: EnumsRole = EnumsRole.USER,
    val email: String,
    val passwordHash: String,
    val name: String,
    val rollNumber: String,
    val branch: String,
    val profilePic: String? = null,
    val profilePicPublicId: String? = null,
    val createdAt: Instant = Instant.now(),
)