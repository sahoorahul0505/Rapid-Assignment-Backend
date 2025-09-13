package com.example.Rapid_Assignment_backend.configuration.errorHandler

class NotFoundException(message: String) :
    AppException("NOT_FOUND", message)

class InvalidRequestException(message: String) :
    AppException("INVALID_REQUEST", message)

class InvalidCredentialException :
    AppException("INVALID_CREDENTIAL", "Invalid email or password")

class ForbiddenException :
    AppException("FORBIDDEN", "You are not allowed to perform this action.")

class UnAuthorizedException :
    AppException("UNAUTHORIZED", "You are not Authorized to access this resource, Token may be Missing")

class ConflictException(entity: String, type: Any) :
    AppException("CONFLICT", "The $entity is already used, please try $type.")