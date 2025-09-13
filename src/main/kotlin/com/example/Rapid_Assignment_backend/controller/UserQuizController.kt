package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.quiz.GetQuestionsRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizSubmissionRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizSubmitResponse
import com.example.Rapid_Assignment_backend.dto.quiz.UserQuestionResponse
import com.example.Rapid_Assignment_backend.services.UserQuizService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quiz/user")
class UserQuizController(
    private val userQuizService: UserQuizService
) {

    val okStatusCode = HttpStatus.OK.value()

    @PostMapping("/get-all-question")
    fun getAllQuestionsForUser(@RequestBody body: GetQuestionsRequest): ResponseEntity<ApiResponse<List<UserQuestionResponse>>> {
        val result = userQuizService.getAllQuestionsForUser(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "All question fetched",
                data = result
            )
        )
    }

    @PostMapping("/submit-quiz")
    fun submitQuizUser(@RequestBody body: QuizSubmissionRequest): ResponseEntity<ApiResponse<QuizSubmitResponse>> {
        val result = userQuizService.submitQuizUser(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "Quiz submitted successfully",
                data = result
            )
        )
    }
}