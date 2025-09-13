package com.example.Rapid_Assignment_backend.dto.common

data class ApiResponse<T>(
    val statusCode : Int,
    val message : String? = null,
    val data : T? = null
)
