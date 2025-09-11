package com.example.Rapid_Assignment_backend.dto.common

data class MailRequest(
    val to : String,
    val subject : String,
    val body : String
)
