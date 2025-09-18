package com.example.Rapid_Assignment_backend.dto.quiz

import jakarta.validation.constraints.NotBlank

data class CreateQuizRequest(
    @field:NotBlank(message = "Subject is required.")
    val subject : String,
    @field:NotBlank(message = "Topic is required.")
    val topic : String
)
