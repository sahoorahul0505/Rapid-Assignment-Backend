package com.example.Rapid_Assignment_backend.domain.model.admin

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


@Document("admin")
data class Admin(
    @Id val id : ObjectId? = null,
    val name : String,
    val email : String,
    val createdAt : Instant = Instant.now()
)
