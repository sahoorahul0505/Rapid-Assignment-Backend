package com.example.Rapid_Assignment_backend.dto

data class ResetPasswordRequest(
    val email : String,
    val otp : String,
    val newPassword : String
)
