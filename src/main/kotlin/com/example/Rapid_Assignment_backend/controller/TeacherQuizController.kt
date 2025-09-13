package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.quiz.AddQuestionRequest
import com.example.Rapid_Assignment_backend.dto.quiz.GetQuestionsRequest
import com.example.Rapid_Assignment_backend.dto.quiz.CreateQuizRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizResponse
import com.example.Rapid_Assignment_backend.dto.quiz.TeacherQuestionsResponse
import com.example.Rapid_Assignment_backend.dto.quiz.TeacherQuizzesResponse
import com.example.Rapid_Assignment_backend.services.TeacherQuizService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/quiz/teacher")
class TeacherQuizController(
    private val quizService: TeacherQuizService
) {

    val okStatusCode = HttpStatus.OK.value()

    @PostMapping("/create-quiz")
    fun createQuiz(
        @RequestBody body: CreateQuizRequest
    ): ResponseEntity<ApiResponse<QuizResponse>> {
        val result = quizService.createQuiz(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "Quiz created successfully",
                data = result
            )
        )
    }

    @PostMapping("/add-question")
    fun addQuestion(@RequestBody body: AddQuestionRequest): ResponseEntity<ApiResponse<Unit>?> {
        quizService.addQuestion(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "Question Added successfully",
            )
        )
    }

    @PostMapping("/get-all-questions")
    fun getAllQuestionsForTeacher(@RequestBody body: GetQuestionsRequest): ResponseEntity<ApiResponse<List<TeacherQuestionsResponse>>> {
        val result = quizService.getQuestionsForTeacher(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "All questions fetched successfully",
                data = result
            )
        )
    }

    @GetMapping("/get-all-quizzes")
    fun getAllQuizzesForTeacher(): ResponseEntity<ApiResponse<List<TeacherQuizzesResponse>> > {
        val result = quizService.getAllQuizzesForTeacher()
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "All quizzes fetched successfully",
                data = result
            )
        )
    }
}