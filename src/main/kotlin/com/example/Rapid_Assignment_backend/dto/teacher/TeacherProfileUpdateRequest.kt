package com.example.Rapid_Assignment_backend.dto.teacher

data class TeacherProfileUpdateRequest(
    val name : String? = null,
    val teacherCode : String? = null,
    val profilePic : String? = null,
    val password : String
)
