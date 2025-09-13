package com.example.Rapid_Assignment_backend.repositories

import com.example.Rapid_Assignment_backend.domain.model.Admin
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository : MongoRepository<Admin, ObjectId>{

}