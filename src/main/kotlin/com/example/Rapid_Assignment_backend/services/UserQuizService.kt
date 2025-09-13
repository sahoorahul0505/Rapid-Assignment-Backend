package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.configuration.SessionContext
import com.example.Rapid_Assignment_backend.configuration.errorHandler.NotFoundException
import com.example.Rapid_Assignment_backend.domain.model.QuizResults
import com.example.Rapid_Assignment_backend.dto.quiz.GetQuestionsRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizSubmissionRequest
import com.example.Rapid_Assignment_backend.dto.quiz.QuizSubmitResponse
import com.example.Rapid_Assignment_backend.dto.quiz.UserQuestionResponse
import com.example.Rapid_Assignment_backend.repositories.QuestionRepository
import com.example.Rapid_Assignment_backend.repositories.QuizRepository
import com.example.Rapid_Assignment_backend.repositories.QuizResultRepository
import com.example.Rapid_Assignment_backend.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserQuizService(
    private val quizRepository: QuizRepository,
    private val quizResultsRepository: QuizResultRepository,
    private val questionRepository: QuestionRepository,
    private val userRepository: UserRepository
) {
    fun getAllQuestionsForUser(request: GetQuestionsRequest): List<UserQuestionResponse> {
        val quizEntity = quizRepository.findByQuizAccessCode(request.quizAccessCode)
            ?: throw NotFoundException("No quiz found with ${request.quizAccessCode}")
        val questionEntity = questionRepository.findAllByQuizId(quizEntity.id!!)
        if (questionEntity.isEmpty()) throw NotFoundException("No question found")

        return questionEntity.map {
            UserQuestionResponse(
                id = it.id?.toHexString(),
                questionText = it.questionText,
                options = it.options
            )
        }
    }

    fun submitQuizUser(request: QuizSubmissionRequest): QuizSubmitResponse {
        val session = SessionContext.getSession()
        val userEntity = userRepository.findById(session.userId).orElseThrow {
            throw NotFoundException("No account found")
        }
        // find quiz
        val quizEntity = quizRepository.findByQuizAccessCode(request.quizAccessCode)
            ?: throw NotFoundException("No quiz found with ${request.quizAccessCode}")
        val questions = questionRepository.findAllByQuizId(quizEntity.id!!)
        if (questions.isEmpty()) {
            throw NotFoundException("No questions found")
        }

        val answerMap = request.answers.associateBy {
            it.questionId
        }
        var totalScore = 0
        var totalMarks = 0
        var correctAnswersCount = 0
        var wrongAnswersCount = 0
        var skippedQuestionsCount = 0
        // evaluate answers
        for (q in questions) {
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
            teacherCode = quizEntity.teacherCode,
            quizId = quizEntity.id.toHexString(),
            quizAccessCode = quizEntity.quizAccessCode,
            userRollNumber = userEntity.rollNumber,
            score = totalScore,
            totalMarks = totalMarks,
            percentage = percentage,
        )
        // save result to DB
        quizResultsRepository.save(result)

        return QuizSubmitResponse(
            quizAccessCode = result.quizAccessCode,
            score = totalScore,
            totalMarks = totalMarks,
            percentage = percentage,
            correctAnswersCount = correctAnswersCount,
            wrongAnswersCount = wrongAnswersCount,
            skippedQuestionsCount = skippedQuestionsCount,
        )
    }
}