package com.example.Rapid_Assignment_backend.domain.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


@Document("questions")
data class Question(
    @Id val id: String? = null,
    val quizId: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val marks : Int,
    val createdAt: Instant = Instant.now()
)
