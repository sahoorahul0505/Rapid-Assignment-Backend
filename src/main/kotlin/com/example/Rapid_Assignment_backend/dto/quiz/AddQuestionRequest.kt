package com.example.Rapid_Assignment_backend.dto.quiz

data class AddQuestionRequest(
    val quizAccessCode : String,
    val questionText : String,
    val options : List<String>,
    val correctOptionIndex : Int,
    val marks : Int
)