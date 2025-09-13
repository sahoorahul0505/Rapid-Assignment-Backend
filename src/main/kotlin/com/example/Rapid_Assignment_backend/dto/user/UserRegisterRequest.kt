package com.example.Rapid_Assignment_backend.dto.user

data class UserRegisterRequest(
    val name : String,
    val email : String,
    val password : String,
    val rollNumber : String,
    val branch : String
)
