package com.example.Rapid_Assignment_backend.configuration.errorHandler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalErrorHandler {

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<ErrorResponse> {
        val status = when (ex.errorCode) {
            "NOT_FOUND" -> HttpStatus.NOT_FOUND
            "INVALID_REQUEST" -> HttpStatus.BAD_REQUEST
            "INVALID_CREDENTIAL" -> HttpStatus.BAD_REQUEST
            "FORBIDDEN" -> HttpStatus.FORBIDDEN
            "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED
            "CONFLICT" -> HttpStatus.CONFLICT
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity(
            ErrorResponse(
                statusCode = status.value(),
                message = ex.message ?: "Unknown Error"
            ), status
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.joinToString {
            "${it.field} : ${it.defaultMessage}"
        }
        val status = HttpStatus.BAD_REQUEST
        return ResponseEntity(
            ErrorResponse(
                status.value(), details
            ), status
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(
            ErrorResponse(
                status.value(), ex.message ?: "Somthing went wrong"
            ), status
        )
    }

}

data class ErrorResponse(
    val statusCode: Int,
    val message: String
)