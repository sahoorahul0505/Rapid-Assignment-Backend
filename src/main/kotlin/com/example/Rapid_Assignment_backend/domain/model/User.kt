package com.example.Rapid_Assignment_backend.domain.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("users")
data class User(
    @Id val id : ObjectId? = null,
    val role : String = "USER",
    val email : String,
    val passwordHash : String,
    val name : String,
    val rollNumber : String,
    val branch : String,
    val profilePic : String? = null,
    val createdAt : Instant = Instant.now()
)