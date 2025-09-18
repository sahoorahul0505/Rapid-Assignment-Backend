package com.example.Rapid_Assignment_backend.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserRegisterRequest(
    @field:NotBlank(message = "Name is required.")
    val name : String,
    @field:NotBlank(message = "Email is required")
    @field:Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
        message = "Invalid email format."
    )
    val email : String,
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#+\\-_.:,^])[A-Za-z\\d@\$!%*?&#+\\-_.:,^]{8,12}$",
        message = "Password must be 8–12 characters long and include uppercase, lowercase, number, and special character"
    )
    val password : String,
    @field:NotBlank(message = "Roll Number is required.")
    val rollNumber : String,
    @field:NotBlank(message = "Branch is required.")
    val branch : String,
    @field:NotBlank(message = "OTP is required.")
    val otp : String
)
