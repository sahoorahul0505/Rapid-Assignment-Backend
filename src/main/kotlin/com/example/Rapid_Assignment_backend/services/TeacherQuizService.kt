package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.ForbiddenException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.UnAuthorizedException
import com.example.Rapid_Assignment_backend.domain.model.Question
import com.example.Rapid_Assignment_backend.domain.model.Quiz
import com.example.Rapid_Assignment_backend.dto.quiz.AddQuestionRequest
import com.example.Rapid_Assignment_backend.dto.quiz.CreateQuizRequest
import com.example.Rapid_Assignment_backend.dto.quiz.GetQuestionsRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizResponse
import com.example.Rapid_Assignment_backend.dto.quiz.TeacherQuestionsResponse
import com.example.Rapid_Assignment_backend.dto.quiz.TeacherQuizzesResponse
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


    fun createQuiz(request: CreateQuizRequest): QuizResponse {
        val session = SessionContext.getSession()
        val teacher = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("Account not found")
        }
        // generate access code
        val quizAccessCode = generateAccessCode(teacher.name, request.topic)
        val quizEntity = Quiz(
            teacherCode = teacher.teacherCode,
            subject = request.subject,
            topic = request.topic,
            quizAccessCode = quizAccessCode
        )

        quizRepository.save(quizEntity)

        return QuizResponse(quizAccessCode)
    }

    fun addQuestion(request: AddQuestionRequest) {
        val session = SessionContext.getSession()
        val teacherEntity = teacherRepository.findById(session.userId).orElseThrow {
            throw NotFoundException("Account not found")
        }
        val quizEntity = quizRepository.findByQuizAccessCodeAndTeacherCode(
            request.quizAccessCode,
            teacherEntity.teacherCode
        ) ?: throw ForbiddenException()
//        val shuffledOptions = request.options.shuffled()
//        val newCorrectIndex = shuffledOptions.indexOf(request.options[request.correctOptionIndex])
        val questionEntity = Question(
            quizId = quizEntity.id!!,
            questionText = request.questionText,
            options = request.options,
            correctOptionIndex = request.correctOptionIndex,
            marks = request.marks
        )
        questionRepository.save(questionEntity)
    }

    fun getQuestionsForTeacher(request: GetQuestionsRequest): List<TeacherQuestionsResponse> {
        val session = SessionContext.getSession()
        val teacherEntity = teacherRepository.findById(session.userId).orElseThrow {
            NotFoundException("No account found")
        }
        val quizEntity = quizRepository.findByQuizAccessCodeAndTeacherCode(
            request.quizAccessCode,
            teacherEntity.teacherCode
        ) ?: throw UnAuthorizedException()
        // fetch all questions
        val questionEntity = questionRepository.findAllByQuizId(quizEntity.id!!)

        // map to response
        return questionEntity.map {
            TeacherQuestionsResponse(
                id = it.id?.toHexString(),
                questionText = it.questionText,
                options = it.options,
                correctOptionIndex = it.correctOptionIndex,
                marks = it.marks
            )
        }
    }

    fun getAllQuizzesForTeacher(): List<TeacherQuizzesResponse> {
        val session = SessionContext.getSession()
        val teacherEntity = teacherRepository.findById(session.userId).orElseThrow {
            IllegalArgumentException("No account found")
        }
        val quizEntity = quizRepository.findAllByTeacherCode(teacherEntity.teacherCode)

        return quizEntity.map {
            TeacherQuizzesResponse(
                subject = it.teacherCode,
                topic = it.topic,
                teacherCode = it.teacherCode,
                quizAccessCode = it.quizAccessCode,
                createdAt = it.createdAt
            )
        }
    }

    private fun generateAccessCode(key1: String, key2: String): String {
        val key1Parts = key1.trim().split(" ")
        val key1Initials = when {
            key1Parts.size >= 2 -> key1Parts.take(key1Parts.size).joinToString("") { it.first().uppercase() }
            else -> key1.take(2).uppercase()
        }

        val key2Parts = key2.trim().split(" ")
        val key2Initials = when {
            key2Parts.size >= 2 -> key2Parts.take(key2Parts.size).joinToString("") { it.first().uppercase() }
            else -> key2.take(2).uppercase()
        }
        val digits = (1000..9999).random().toString()

        return key1Initials + key2Initials + digits
    }


}