package com.example.Rapid_Assignment_backend.dto.user

data class UserRegisterRequest(
    val email : String,
    val password : String,
    val name : String,
    val rollNumber : String,
    val branch : String,
)
