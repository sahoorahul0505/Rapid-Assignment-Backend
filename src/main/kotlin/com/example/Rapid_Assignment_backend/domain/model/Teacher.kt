package com.example.Rapid_Assignment_backend.domain.model

import com.example.Rapid_Assignment_backend.utils.EnumsRole
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("teacher")
data class Teacher(
    @Id val id: String? = null,
    val role: EnumsRole = EnumsRole.TEACHER,
    val name: String,
    val email: String,
    val passwordHash: String,
    val teacherCode: String,
    val profilePic: String? = null,
    val profilePicPublicId: String? = null,
    val approved: Boolean = false,
    val createdAt: Instant = Instant.now()
)
