package com.example.Rapid_Assignment_backend.dto.quiz

data class UserQuizQuestionResponse(
    val questionId : String?,
    val questionText : String,
    val options : List<String>
)
