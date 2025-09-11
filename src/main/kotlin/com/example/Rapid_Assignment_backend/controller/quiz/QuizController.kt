package com.example.Rapid_Assignment_backend.controller.quiz

import com.example.Rapid_Assignment_backend.controller.getHeader
import com.example.Rapid_Assignment_backend.dto.quiz.QuizRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizResponse
import com.example.Rapid_Assignment_backend.services.quiz.QuizService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/quiz/teacher")
class QuizController(
    private val quizService: QuizService
) {

    @PostMapping("/create-quiz")
    fun createQuiz(
        @RequestBody body: QuizRequest
    ): ResponseEntity<QuizResponse> {
        val response = quizService.createQuiz(body)
        return ResponseEntity.ok(QuizResponse(response))
    }
}