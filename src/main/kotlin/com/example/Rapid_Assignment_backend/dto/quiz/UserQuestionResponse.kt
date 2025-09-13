package com.example.Rapid_Assignment_backend.dto.quiz

data class UserQuestionResponse(
    val id : String?,
    val questionText : String,
    val options : List<String>
)
