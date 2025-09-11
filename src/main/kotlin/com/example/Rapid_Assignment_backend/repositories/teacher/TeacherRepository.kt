package com.example.Rapid_Assignment_backend.repositories.teacher

import com.example.Rapid_Assignment_backend.domain.model.teacher.Teacher
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository : MongoRepository<Teacher, ObjectId>{
    fun findByEmail(email : String) : Teacher?
}