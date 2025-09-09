package com.example.Rapid_Assignment_backend.dto

data class ProfileUpdateRequest(
    val password : String,
    val name : String? = null,
    val rollNumber : String? = null,
    val branch : String? = null,
    val profilePic : String? = null
)
