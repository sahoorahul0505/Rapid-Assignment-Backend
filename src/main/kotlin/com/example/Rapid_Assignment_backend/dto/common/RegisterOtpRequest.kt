package com.example.Rapid_Assignment_backend.dto.common

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class RegisterOtpRequest(
    @field:NotBlank(message = "Name is required.")
    val name : String,
    @field:NotBlank(message = "Email is required.")
    @field:Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
        message = "Invalid email format."
    )
    val email : String
)