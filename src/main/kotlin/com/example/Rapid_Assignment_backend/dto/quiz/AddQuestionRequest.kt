package com.example.Rapid_Assignment_backend.dto.quiz

import jakarta.validation.constraints.NotBlank

data class AddQuestionRequest(
    @field:NotBlank(message = "Question is required.")
    val questionText : String,
    @field:NotBlank(message = "Minimum two options are required.")
    val options : List<String>,
    @field:NotBlank(message = "Correct option index is required.")
    val correctOptionIndex : Int,
    @NotBlank(message = "Marks is required.")
    val marks : Int
)