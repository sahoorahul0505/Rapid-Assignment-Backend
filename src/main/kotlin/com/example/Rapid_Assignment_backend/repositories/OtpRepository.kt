package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.Otp
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OtpRepository : MongoRepository<Otp, ObjectId>{
    fun findByEmail(email : String) : Otp?
    fun deleteByEmail(email : String)
    fun findByType(type : String) : Otp?
}