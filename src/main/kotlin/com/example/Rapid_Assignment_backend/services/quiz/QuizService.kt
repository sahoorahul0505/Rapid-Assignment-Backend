package com.example.Rapid_Assignment_backend.services.quiz

import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.domain.model.common.Session
import com.example.Rapid_Assignment_backend.domain.model.quiz.Quiz
import com.example.Rapid_Assignment_backend.dto.quiz.QuizRequest
import com.example.Rapid_Assignment_backend.repositories.SessionRepository
import com.example.Rapid_Assignment_backend.repositories.quiz.QuizRepository
import com.example.Rapid_Assignment_backend.repositories.teacher.TeacherRepository
import jakarta.servlet.http.HttpServletRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val teacherRepository: TeacherRepository
) {

    fun createQuiz(request: QuizRequest): String {
        val session = SessionContext.getSession()
        val teacher = teacherRepository.findById(session.userId).orElseThrow {
            NoSuchElementException("No account found")
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

        return quizAccessCode
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