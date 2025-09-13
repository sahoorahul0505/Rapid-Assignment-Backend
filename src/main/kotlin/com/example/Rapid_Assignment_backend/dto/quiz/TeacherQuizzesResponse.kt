package com.example.Rapid_Assignment_backend.dto.quiz

import java.time.Instant

data class TeacherQuizzesResponse(
    val subject : String,
    val topic : String,
    val teacherCode : String,
    val quizAccessCode : String,
    val createdAt : Instant
)
