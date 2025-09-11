package com.example.Rapid_Assignment_backend.configuration

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse(success = false, message = ex.message ?: "Bad request"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse(success = false, message = ex.message ?: "Resource not found"))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(ex : UnauthorizedException) : ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse(success = false, message = ex.message ?: "Unauthorized"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ApiResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(success = false, message = ex.message ?: "Something went wrong, please try again"))
    }

}

class UnauthorizedException(message : String) : RuntimeException(message)