package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.Teacher
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository : MongoRepository<Teacher, String>{
    fun findByEmail(email : String) : Teacher?
    fun findByTeacherCode(teacherCode : String) : Teacher?
}