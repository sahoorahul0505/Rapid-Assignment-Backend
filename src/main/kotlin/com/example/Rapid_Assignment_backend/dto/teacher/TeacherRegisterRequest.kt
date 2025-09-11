package com.example.Rapid_Assignment_backend.dto.teacher

data class TeacherRegisterRequest(
    val name : String,
    val email : String,
    val password : String,
    val teacherCode : String,
)
