package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.BadRequestException
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.domain.model.QuizResults
import com.example.Rapid_Assignment_backend.dto.quiz.AnswerRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizSubmitResponse
import com.example.Rapid_Assignment_backend.dto.quiz.UserQuizQuestionResponse
import com.example.Rapid_Assignment_backend.dto.quiz.UsersQuizResponse
import com.example.Rapid_Assignment_backend.repositories.QuestionRepository
import com.example.Rapid_Assignment_backend.repositories.QuizRepository
import com.example.Rapid_Assignment_backend.repositories.QuizResultRepository
import com.example.Rapid_Assignment_backend.repositories.TeacherRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserQuizService(
    private val quizRepository: QuizRepository,
    private val quizResultsRepository: QuizResultRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository,
    private val teacherRepository: TeacherRepository
) {

    fun getQuizForUser(quizCode: String): UsersQuizResponse {
        val savedQuiz = quizRepository.findByQuizCode(quizCode)
            ?: throw NotFoundException("No quiz found with $quizCode")
        val savedTeacher = teacherRepository.findByTeacherCode(savedQuiz.teacherCode)
            ?: throw NotFoundException("Teacher not found")
        val quizId = savedQuiz.id ?: throw BadRequestException("Quiz Id is missing.")
        val savedQuestion = questionRepository.findAllByQuizId(quizId)
        val totalMarks = savedQuestion.sumOf { it.marks }
        println("total marks : $totalMarks")
        return UsersQuizResponse(
            subject = savedQuiz.subject,
            topic = savedQuiz.topic,
            quizCode = savedQuiz.quizCode,
            teacherName = savedTeacher.name,
            totalMarks = totalMarks
        )
    }


    fun getQuizQuestionsForUser(quizCode: String): List<UserQuizQuestionResponse> {
        val savedQuiz = quizRepository.findByQuizCode(quizCode)
            ?: throw NotFoundException("No quiz found with $quizCode")
        val savedQuestions = questionRepository.findAllByQuizId(savedQuiz.id!!)
        if (savedQuestions.isEmpty()) throw NotFoundException("No questions found")

        return savedQuestions.map {
            UserQuizQuestionResponse(
                questionId = it.id,
                questionText = it.questionText,
                options = it.options
            )
        }
    }

    fun submitQuizUser(quizCode: String, answers: List<AnswerRequest>): QuizSubmitResponse {
        val session = SessionContext.getSession()
        val savedUser = userRepository.findById(session.userId).orElseThrow {
            throw NotFoundException("No account found.")
        }
        // find quiz
        val savedQuiz = quizRepository.findByQuizCode(quizCode)
            ?: throw NotFoundException("No quiz found with $quizCode")
        val saveQuestions = questionRepository.findAllByQuizId(savedQuiz.id!!)
        if (saveQuestions.isEmpty()) {
            throw NotFoundException("No questions found")
        }

        val answerMap = answers.associateBy {
            it.questionId
        }
        var totalScore = 0
        var totalMarks = 0
        var correctAnswersCount = 0
        var wrongAnswersCount = 0
        var skippedQuestionsCount = 0
        // evaluate answers
        for (q in saveQuestions) {
            totalMarks += q.marks // because Each Question can have different marks.
            val answer = answerMap[q.id.toString()]
            when {
                answer == null -> {
                    skippedQuestionsCount++
                }

                answer.selectedOptionIndex == q.correctOptionIndex -> {
                    correctAnswersCount++
                    totalScore += q.marks
                }

                else -> {
                    wrongAnswersCount++
                }
            }
        }

        val percentage: Double = if (totalMarks > 0) (totalScore.toDouble() / totalMarks) * 100 else 0.0
        val result = QuizResults(
            teacherCode = savedQuiz.teacherCode,
            quizId = savedQuiz.id.toString(),
            quizCode = savedQuiz.quizCode,
            userRollNumber = savedUser.rollNumber,
            score = totalScore,
            totalMarks = totalMarks,
            percentage = percentage,
        )
        // save result to DB
        quizResultsRepository.save(result)

        return QuizSubmitResponse(
            quizCode = result.quizCode,
            score = totalScore,
            totalMarks = totalMarks,
            percentage = percentage,
            correctAnswersCount = correctAnswersCount,
            wrongAnswersCount = wrongAnswersCount,
            skippedQuestionsCount = skippedQuestionsCount,
        )
    }
}