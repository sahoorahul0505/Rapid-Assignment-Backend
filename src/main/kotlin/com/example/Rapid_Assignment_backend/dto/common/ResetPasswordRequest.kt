package com.example.Rapid_Assignment_backend.dto.common

data class ResetPasswordRequest(
    val email : String,
    val newPassword : String
)
