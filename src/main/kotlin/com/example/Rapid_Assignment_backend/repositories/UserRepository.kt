package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.user.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, ObjectId> {
    fun findByEmail(email: String) : User?
}

