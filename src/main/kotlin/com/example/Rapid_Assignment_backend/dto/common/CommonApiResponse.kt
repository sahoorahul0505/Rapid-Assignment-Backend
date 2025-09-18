package com.example.Rapid_Assignment_backend.dto.common

data class CustomApiResponse<T>(
    val statusCode : Int,
    val message : String,
    val data : T? = null
)
