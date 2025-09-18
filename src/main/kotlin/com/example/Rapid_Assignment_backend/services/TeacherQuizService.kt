package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.UnauthorizedException
import com.example.Rapid_Assignment_backend.domain.model.Question
import com.example.Rapid_Assignment_backend.domain.model.Quiz
import com.example.Rapid_Assignment_backend.utils.toReadableTime
import com.example.Rapid_Assignment_backend.dto.quiz.*
import com.example.Rapid_Assignment_backend.repositories.QuestionRepository
import com.example.Rapid_Assignment_backend.repositories.QuizRepository
import com.example.Rapid_Assignment_backend.repositories.TeacherRepository
import org.springframework.stereotype.Service

@Service
class TeacherQuizService(
    private val quizRepository: QuizRepository,
    private val questionRepository: QuestionRepository,
    private val teacherRepository: TeacherRepository
) {


    fun createQuiz(request: CreateQuizRequest): String {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            UnauthorizedException("Please login.")
        }
        // generate access code
        val quizCode = generateAccessCode(request.subject, request.topic)
        val quizEntity = Quiz(
            teacherCode = savedTeacher.teacherCode,
            subject = request.subject,
            topic = request.topic,
            quizCode = quizCode
        )

        quizRepository.save(quizEntity)

        return quizCode
    }

    fun addQuestionsToQuiz(quizCode: String, request: List<AddQuestionRequest>) {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            throw UnauthorizedException("Please login.")
        }
        val savedQuiz = quizRepository.findByQuizCodeAndTeacherCode(
            quizCode,
            savedTeacher.teacherCode
        ) ?: throw UnauthorizedException("Access denied. You are not allowed.")
//        val shuffledOptions = request.options.shuffled()
//        val newCorrectIndex = shuffledOptions.indexOf(request.options[request.correctOptionIndex])
        request.forEach { q ->
            val questionEntity = Question(
                quizId = savedQuiz.id!!,
                questionText = q.questionText,
                options = q.options,
                correctOptionIndex = q.correctOptionIndex,
                marks = q.marks
            )
            questionRepository.save(questionEntity)
        }
    }

    fun getQuizQuestionsForTeacher(quizCode : String): List<TeacherQuizQuestionResponse> {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found")
        }
        val savedQuiz = quizRepository.findByQuizCodeAndTeacherCode(
            quizCode,
            savedTeacher.teacherCode
        ) ?: throw UnauthorizedException("You are not allowed access to this resource.")
        // fetch all questions
        val savedQuestions = questionRepository.findAllByQuizId(savedQuiz.id!!)

        // map to response
        return savedQuestions.map {
            TeacherQuizQuestionResponse(
                id = it.id,
                questionText = it.questionText,
                options = it.options,
                correctOptionIndex = it.correctOptionIndex,
                marks = it.marks
            )
        }
    }

    fun getAllQuizzesForTeacher(): List<TeacherQuizResponse> {
        val session = SessionContext.getSession()
        val savedTeacher = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found")
        }
        val savedQuiz = quizRepository.findAllByTeacherCode(savedTeacher.teacherCode)

        return savedQuiz.map {
            TeacherQuizResponse(
                subject = it.subject,
                topic = it.topic,
                quizCode = it.quizCode,
                createdAt = it.createdAt.toReadableTime()
            )
        }
    }

    private fun generateAccessCode(key1: String, key2: String): String {

        val key1Parts = key1.trim().split(" ")
        val key1Initials = when {
            key1Parts.size >= 2 -> key1Parts.take(key1Parts.size).joinToString("") { it.first().uppercase() }
            else -> key1.take(3).uppercase()
        }

        val key2Parts = key2.trim().split(" ")
        val key2Initials = when {
            key2Parts.size >= 2 -> key2Parts.take(key2Parts.size).joinToString("") { it.first().uppercase() }
            else -> key2.take(3).uppercase()
        }

        val digits = (1000..9999).random().toString()

        return key1Initials + key2Initials + digits
    }


}