package com.example.Rapid_Assignment_backend.repositories.quiz

import com.example.Rapid_Assignment_backend.domain.model.quiz.Quiz
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : MongoRepository<Quiz, ObjectId>{
//    fun findByQuizAccessCode(quizAccessCode : String) : Quiz?
}