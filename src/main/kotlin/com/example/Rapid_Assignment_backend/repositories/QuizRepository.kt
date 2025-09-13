package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.Quiz
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : MongoRepository<Quiz, ObjectId>{
    fun findByQuizAccessCode(quizAccessCode: String) : Quiz?
    fun findAllByTeacherCode(teacherCode: String) : List<Quiz>
    fun findByQuizAccessCodeAndTeacherCode(quizAccessCode: String, teacherCode : String) : Quiz?
}