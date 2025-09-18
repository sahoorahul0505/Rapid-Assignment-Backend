package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.CustomApiResponse
import com.example.Rapid_Assignment_backend.dto.quiz.AddQuestionRequest
import com.example.Rapid_Assignment_backend.dto.quiz.CreateQuizRequest
import com.example.Rapid_Assignment_backend.dto.quiz.TeacherQuizQuestionResponse
import com.example.Rapid_Assignment_backend.dto.quiz.TeacherQuizResponse
import com.example.Rapid_Assignment_backend.services.TeacherQuizService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/teachers/quizzes")
class TeacherQuizController(
    private val quizService: TeacherQuizService
) {

    val okStatusCode = HttpStatus.OK.value()

    @PostMapping("/create")
    fun createQuiz(
        @Valid
        @RequestBody body: CreateQuizRequest
    ): ResponseEntity<CustomApiResponse<String>> {
        val result = quizService.createQuiz(body)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            CustomApiResponse(
                statusCode = HttpStatus.CREATED.value(),
                message = "Quiz created successfully",
                data = result
            )
        )
    }

    @PostMapping("/{quizCode}/add-questions")
    fun addQuestion(
        @PathVariable quizCode: String,
        @Valid
        @RequestBody body: List<AddQuestionRequest>
    ): ResponseEntity<CustomApiResponse<Unit>?> {
        quizService.addQuestionsToQuiz(quizCode, body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Question Added successfully",
            )
        )
    }

    @GetMapping("/{quizCode}/questions")
    fun getQuizQuestionsForTeacher(@PathVariable quizCode: String): ResponseEntity<CustomApiResponse<List<TeacherQuizQuestionResponse>>> {
        val result = quizService.getQuizQuestionsForTeacher(quizCode)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Questions fetched successfully",
                data = result
            )
        )
    }

    @GetMapping
    fun getAllQuizzesForTeacher(): ResponseEntity<CustomApiResponse<List<TeacherQuizResponse>>> {
        val result = quizService.getAllQuizzesForTeacher()
        val message = if (result.isEmpty()) "No quizzes created." else "Quizzes fetched successfully"
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = message,
                data = result
            )
        )
    }
}