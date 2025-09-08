package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.user.PasswordResetOtp
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.objenesis.Objenesis
import org.springframework.stereotype.Repository


@Repository
interface PasswordResetOtpRepository : MongoRepository<PasswordResetOtp, Objenesis> {

    fun findByUserId(userId : ObjectId) : PasswordResetOtp?
    fun deleteByUserId(userId : ObjectId)
}