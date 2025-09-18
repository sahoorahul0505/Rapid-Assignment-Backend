package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.Question
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : MongoRepository<Question, String> {
//    fun findBySubjectContainingIgnoreCase(subject : String) : List<Question?>
    fun findAllByQuizId(quizId : String) : List<Question>
    fun findByQuizId(quizId : String) : Question?
}