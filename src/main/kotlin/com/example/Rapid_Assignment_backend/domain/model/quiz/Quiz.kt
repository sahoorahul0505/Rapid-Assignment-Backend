package com.example.Rapid_Assignment_backend.domain.model.quiz

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("quizzes")
data class Quiz(
    @Id val id: ObjectId? = null,
    val teacherCode: String,
    val subject: String,
    val topic: String,
    val quizAccessCode: String,                 // 8-char unique
    val createdAt: Instant = Instant.now()
)
