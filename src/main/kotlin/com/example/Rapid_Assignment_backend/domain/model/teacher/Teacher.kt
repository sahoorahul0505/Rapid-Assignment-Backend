package com.example.Rapid_Assignment_backend.domain.model.teacher

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("teacher")
data class Teacher(
   @Id val id : ObjectId? = null,
    val role : String = "TEACHER",
    val name : String,
    val email : String,
    val passwordHash : String,
    val teacherCode : String,
    val profilePic : String? = null,
    val approved : Boolean = false,
    val createdAt : Instant = Instant.now()
)
