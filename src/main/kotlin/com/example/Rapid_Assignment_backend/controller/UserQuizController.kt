package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.CustomApiResponse
import com.example.Rapid_Assignment_backend.dto.quiz.AnswerRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizSubmitResponse
import com.example.Rapid_Assignment_backend.dto.quiz.UserQuizQuestionResponse
import com.example.Rapid_Assignment_backend.dto.quiz.UsersQuizResponse
import com.example.Rapid_Assignment_backend.services.UserQuizService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/quizzes")
class UserQuizController(
    private val userQuizService: UserQuizService
) {

    val okStatusCode = HttpStatus.OK.value()

    @GetMapping("/{quizCode}")
    fun getQuizForUser(@PathVariable quizCode: String): ResponseEntity<CustomApiResponse<UsersQuizResponse>> {
        val result = userQuizService.getQuizForUser(quizCode)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Quiz fetched",
                data = result
            )
        )
    }

    @GetMapping("/{quizCode}/questions")
    fun getQuizQuestionsForUser(@PathVariable quizCode: String): ResponseEntity<CustomApiResponse<List<UserQuizQuestionResponse>>> {
        val result = userQuizService.getQuizQuestionsForUser(quizCode)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "All question fetched",
                data = result
            )
        )
    }

    @PostMapping("/{quizCode}/submission")
    fun submitQuizForUser(
        @PathVariable quizCode: String,
        @RequestBody body: List<AnswerRequest>
    ): ResponseEntity<CustomApiResponse<QuizSubmitResponse>> {
        val result = userQuizService.submitQuizUser(quizCode, body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Quiz submitted successfully",
                data = result
            )
        )
    }
}