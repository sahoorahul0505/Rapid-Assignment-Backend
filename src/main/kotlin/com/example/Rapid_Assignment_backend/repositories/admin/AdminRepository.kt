package com.example.Rapid_Assignment_backend.repositories.admin

import com.example.Rapid_Assignment_backend.domain.model.admin.Admin
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository : MongoRepository<Admin, ObjectId>{

}