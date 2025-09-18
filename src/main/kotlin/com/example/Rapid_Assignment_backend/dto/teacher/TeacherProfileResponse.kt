package com.example.Rapid_Assignment_backend.dto.teacher

data class TeacherProfileResponse(
    val email : String,
    val name : String,
    val teacherCode : String,
    val profilePic : String? = null
)
