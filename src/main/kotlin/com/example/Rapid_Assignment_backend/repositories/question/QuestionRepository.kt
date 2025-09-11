package com.example.Rapid_Assignment_backend.repositories.question

import com.example.Rapid_Assignment_backend.domain.model.quiz.Question
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : MongoRepository<Question, ObjectId> {
//    fun findBySubjectContainingIgnoreCase(subject : String) : List<Question>
}