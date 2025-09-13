package com.example.Rapid_Assignment_backend.domain.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("quiz_results")
data class QuizResults(
    @Id val id : ObjectId? = null,
    val teacherCode : String,
    val quizId : String,
    val quizAccessCode : String,
    val userRollNumber : String,
    val score : Int,
    val totalMarks : Int,
    val percentage: Double,
    val submittedAt : Instant = Instant.now()
)
