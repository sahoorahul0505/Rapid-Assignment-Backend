package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.QuizResults
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizResultRepository : MongoRepository<QuizResults, ObjectId>{
    fun findByQuizAccessCode(quizAccessCode : String) : QuizResults?
    fun findByQuizIdAndUserRollNumber(quizId: String, userRollNumber: String) : QuizResults?
}