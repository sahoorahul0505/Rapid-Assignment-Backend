package com.example.Rapid_Assignment_backend.dto.quiz

data class QuizSubmitResponse(
    val quizAccessCode : String,
    val score : Int,
    val totalMarks : Int,
    val percentage : Double,
    val correctAnswersCount : Int,
    val wrongAnswersCount : Int,
    val skippedQuestionsCount : Int
)
