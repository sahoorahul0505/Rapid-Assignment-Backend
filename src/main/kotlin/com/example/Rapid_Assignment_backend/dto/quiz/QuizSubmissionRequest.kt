package com.example.Rapid_Assignment_backend.dto.quiz

data class QuizSubmissionRequest(
    val quizAccessCode : String,
    val answers : List<AnswerRequest>
)
