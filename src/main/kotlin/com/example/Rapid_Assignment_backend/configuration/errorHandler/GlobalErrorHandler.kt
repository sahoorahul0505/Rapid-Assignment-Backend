package com.example.Rapid_Assignment_backend.configuration.errorHandler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalErrorHandler {

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(statusCode = HttpStatus.UNAUTHORIZED.value(), message = ex.message ?: "Unauthorized"),
            HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = ex.message ?: "Not Found"),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(statusCode = HttpStatus.BAD_REQUEST.value(), message = ex.message ?: "Bad Request"),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(statusCode = HttpStatus.CONFLICT.value(), message = ex.message ?: "Conflict"),
            HttpStatus.CONFLICT
        )
    }

    // handle json parse or field missmatch error
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParser(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                statusCode = HttpStatus.BAD_REQUEST.value(),
                message = e.message ?: "Malformed request or field mismatch"
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    // handle validation error
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = e.bindingResult.fieldErrors.joinToString { "${it.field} : ${it.defaultMessage}" }
        return ResponseEntity(
            ErrorResponse(statusCode = HttpStatus.BAD_REQUEST.value(), message = "Validation failed : $details"),
            HttpStatus.BAD_REQUEST
        )
    }

    // catch all fall back for anything else
    @ExceptionHandler(Exception::class)
    fun handleGeneric(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = e.message ?: "Internal Server Error"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    // Invalid endpoint exception
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFoundEndpoint(e: NoHandlerFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(statusCode = HttpStatus.NOT_FOUND.value(), message = "Endpoint not found: ${e.requestURL}"),
            HttpStatus.NOT_FOUND
        )
    }
}

data class ErrorResponse(
    val statusCode: Int,
    val message: String
)