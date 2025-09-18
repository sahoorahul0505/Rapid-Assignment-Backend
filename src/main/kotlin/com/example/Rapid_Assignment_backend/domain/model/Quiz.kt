package com.example.Rapid_Assignment_backend.domain.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("quizzes")
data class Quiz(
    @Id val id: String? = null,
    val teacherCode: String,
    val subject: String,
    val topic: String,
    val quizCode: String,                 // 8-char unique
    val createdAt: Instant = Instant.now()
)
