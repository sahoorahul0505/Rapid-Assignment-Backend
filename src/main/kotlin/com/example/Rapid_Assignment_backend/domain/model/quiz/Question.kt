package com.example.Rapid_Assignment_backend.domain.model.quiz

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


@Document("questions")
data class Question(
    @Id val id: ObjectId? = null,
    val quizId: ObjectId,                   // kis quiz se linked hai
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val createdAt: Instant = Instant.now()
)
