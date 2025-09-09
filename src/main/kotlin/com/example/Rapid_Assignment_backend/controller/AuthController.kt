package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.ApiResponse
import com.example.Rapid_Assignment_backend.dto.ForgotPasswordRequest
import com.example.Rapid_Assignment_backend.dto.LoginRequest
import com.example.Rapid_Assignment_backend.dto.LoginResponse
import com.example.Rapid_Assignment_backend.dto.RegisterRequest
import com.example.Rapid_Assignment_backend.dto.ResetPasswordRequest
import com.example.Rapid_Assignment_backend.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody body: RegisterRequest): ResponseEntity<ApiResponse> {
        val response = authService.register(body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(body)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest): ResponseEntity<ApiResponse> {
        val token = getHeader(httpServletRequest)
        authService.logOut(token)
        return ResponseEntity.ok(ApiResponse(success = true, message = "Logged out successfully"))
    }

    @PostMapping("/forgot-password")
    fun forgotPassword(@RequestBody body: ForgotPasswordRequest): ResponseEntity<ApiResponse> {
        val response = authService.sendPasswordResetOtp(body.email)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/reset=password-otp")
    fun resetPasswordWithOtp(@RequestBody body: ResetPasswordRequest): ResponseEntity<ApiResponse> {
        val response = authService.resetPasswordWithOtp(request = body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }
}