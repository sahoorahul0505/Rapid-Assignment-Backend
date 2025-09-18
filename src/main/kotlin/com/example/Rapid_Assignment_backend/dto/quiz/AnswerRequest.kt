package com.example.Rapid_Assignment_backend.dto.quiz

data class AnswerRequest(
    val questionId : String ,
    val selectedOptionIndex : Int? = null
)
