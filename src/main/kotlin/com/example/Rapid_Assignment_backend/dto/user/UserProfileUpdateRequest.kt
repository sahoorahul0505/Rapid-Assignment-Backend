package com.example.Rapid_Assignment_backend.dto.user

data class UserProfileUpdateRequest(
    val name : String? = null,
    val rollNumber : String? = null,
    val branch : String? = null,
    val profilePic : String? = null,
    val password : String
)
