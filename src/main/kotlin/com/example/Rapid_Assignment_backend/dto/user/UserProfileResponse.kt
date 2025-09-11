package com.example.Rapid_Assignment_backend.dto.user

data class UserProfileResponse(
    val email : String,
    val name : String,
    val rollNumber : String,
    val branch : String,
    val profilePic : String? = null
)
