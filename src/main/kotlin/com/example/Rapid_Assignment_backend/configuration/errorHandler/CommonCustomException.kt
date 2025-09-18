package com.example.Rapid_Assignment_backend.configuration.errorHandler

class UnauthorizedException(message: String) : RuntimeException(message)
class ForbiddenException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)
