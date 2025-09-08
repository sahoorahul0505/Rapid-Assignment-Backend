package com.example.Rapid_Assignment_backend.dto

data class RegisterRequest(
    val email : String,
    val password : String,
    val name : String,
    val rollNumber : String,
    val branch : String,
)
