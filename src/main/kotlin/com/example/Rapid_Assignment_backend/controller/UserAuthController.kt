package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.common.ForgotPasswordRequest
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.ResetPasswordRequest
import com.example.Rapid_Assignment_backend.dto.common.SessionResponse
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterRequest
import com.example.Rapid_Assignment_backend.services.UserAuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/user")
class UserAuthController(
    private val userAuthService: UserAuthService
) {
    val okStatusCode = HttpStatus.OK.value()

    @PostMapping("/send-registration-otp")
    fun sendOtpForRegistration(@RequestBody body: UserRegisterOtpRequest): ResponseEntity<ApiResponse<Unit>?> {
        userAuthService.sendOtpForRegistrationUser(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "OTP sent to your Email"
            )
        )
    }

    @PostMapping("/register")
    fun register(
        @RequestBody body: UserRegisterRequest,
        @RequestParam otp: String
    ): ResponseEntity<ApiResponse<SessionResponse>> {
        val result = userAuthService.verifyOtpAndRegisterUser(body, otp)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "Registration successful",
                data = result
            )
        )
    }

    @PostMapping("/send-login-otp")
    fun sendOtpForLogin(@RequestBody body: CommonLoginRequest): ResponseEntity<ApiResponse<Unit>?> {
        userAuthService.sendOtpForLogin(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "OTP sent to your Email"
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody body: CommonLoginRequest, @RequestParam otp: String): ResponseEntity<ApiResponse<SessionResponse> > {
        val result = userAuthService.verifyOtpAndLogin(body, otp)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "Login successful",
                data = result
            )
        )
    }


    @PostMapping("/forgot-password-otp")
    fun sendOtpForForgotPassword(@RequestBody body: ForgotPasswordRequest): ResponseEntity<ApiResponse<Unit>?> {
        userAuthService.sendOtpForForgotPassword(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "OTP sent to your Email"
            )
        )
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody body: ResetPasswordRequest,
        @RequestParam otp: String
    ): ResponseEntity<ApiResponse<Unit>?> {
        userAuthService.validateOtpAndResetPassword(request = body, otp)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatusCode,
                message = "Password update successfully, We have sent password to your Email."
            )
        )
    }
}