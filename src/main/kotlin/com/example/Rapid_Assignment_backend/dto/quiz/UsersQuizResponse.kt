package com.example.Rapid_Assignment_backend.dto.quiz

data class UsersQuizResponse(
    val subject : String,
    val topic : String,
    val quizCode : String,
    val teacherName : String,
    val totalMarks : Int
)
