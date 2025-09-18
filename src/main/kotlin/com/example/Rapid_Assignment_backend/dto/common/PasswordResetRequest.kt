package com.example.Rapid_Assignment_backend.dto.common

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class PasswordResetRequest(
    @field:NotBlank(message = "Email is required.")
    @field:Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
        message = "Invalid email format."
    )
    val email : String,
    @field:NotBlank(message = "New password is required.")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&#+\\-_.:,^])[A-Za-z\\d@\$!%*?&#+\\-_.:,^]{8,12}$",
        message = "Password must be 8–12 characters long and include uppercase, lowercase, number, and special character."
    )
    val newPassword : String,
    @field:NotBlank(message = "OTP is required.")
    val otp : String
)
