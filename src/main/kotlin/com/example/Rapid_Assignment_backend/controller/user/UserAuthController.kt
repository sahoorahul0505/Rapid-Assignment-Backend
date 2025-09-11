package com.example.Rapid_Assignment_backend.controller.user

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.common.ForgotPasswordRequest
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginResponse
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterRequest
import com.example.Rapid_Assignment_backend.dto.common.ResetPasswordRequest
import com.example.Rapid_Assignment_backend.services.user.UserAuthService
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

    @PostMapping("/send-registration-otp")
    fun sendOtpForRegistration(@RequestBody body: UserRegisterRequest): ResponseEntity<ApiResponse> {
        val response = userAuthService.sendOtpForRegistration(body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/register")
    fun register(@RequestBody body: UserRegisterRequest, @RequestParam otp: String): ResponseEntity<ApiResponse> {
        val response = userAuthService.verifyOtpAndRegister(body, otp)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/send-login-otp")
    fun sendOtpForLogin(@RequestBody body: CommonLoginRequest): ResponseEntity<ApiResponse> {
        val response = userAuthService.sendOtpForLogin(body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: CommonLoginRequest, @RequestParam otp: String): ResponseEntity<CommonLoginResponse> {
        val response = userAuthService.verifyOtpAndLogin(body, otp)
        return ResponseEntity.ok(response)
    }


    @PostMapping("/forgot-password-otp")
    fun sendOtpForForgotPassword(@RequestBody body: ForgotPasswordRequest): ResponseEntity<ApiResponse> {
        val response = userAuthService.sendOtpForForgotPassword(body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody body: ResetPasswordRequest,
        @RequestParam otp: String
    ): ResponseEntity<ApiResponse> {
        val response = userAuthService.validateOtpAndResetPassword(request = body, otp)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }
}