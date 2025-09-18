package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.Quiz
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizRepository : MongoRepository<Quiz, String>{
    fun findByQuizCode(quizCode: String) : Quiz?
    fun findAllByTeacherCode(teacherCode: String) : List<Quiz>
    fun findByQuizCodeAndTeacherCode(quizCode: String, teacherCode : String) : Quiz?
}