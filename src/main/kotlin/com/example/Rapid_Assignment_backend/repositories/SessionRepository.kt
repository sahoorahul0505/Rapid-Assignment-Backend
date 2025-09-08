package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.user.Session
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository : MongoRepository<Session, String>{
    fun findByToken(token : String) : Session?
    fun deleteByUserId(userId: ObjectId)
}