package com.example.Rapid_Assignment_backend.configuration.errorHandler

open class AppException(
    val errorCode: String,
    override val message: String?
) : RuntimeException(message) {

}