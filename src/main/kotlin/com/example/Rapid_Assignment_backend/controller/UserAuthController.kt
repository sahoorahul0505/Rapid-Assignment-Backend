package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.CustomApiResponse
import com.example.Rapid_Assignment_backend.dto.common.LoginOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.LoginRequest
import com.example.Rapid_Assignment_backend.dto.common.RegisterOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.PasswordResetRequest
import com.example.Rapid_Assignment_backend.dto.common.PaswrdResetOtpRequest
import com.example.Rapid_Assignment_backend.dto.common.SessionResponse
import com.example.Rapid_Assignment_backend.dto.user.UserRegisterRequest
import com.example.Rapid_Assignment_backend.services.UserAuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/auth")
class UserAuthController(
    private val userAuthService: UserAuthService
) {
    val okStatusCode = HttpStatus.OK.value()

    @PostMapping("/register/otp")
    fun sendOtpForRegistration(
        @Valid
        @RequestBody body: RegisterOtpRequest
    ): ResponseEntity<CustomApiResponse<Unit>> {
        userAuthService.sendOtpForRegistrationUser(body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "OTP sent to your Email"
            )
        )
    }

    @PostMapping("/register")
    fun register(
        @Valid
        @RequestBody body: UserRegisterRequest,
    ): ResponseEntity<CustomApiResponse<SessionResponse>> {
        val result = userAuthService.verifyOtpAndRegisterUser(body) //(UserAuthController.kt:42)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                CustomApiResponse(
                    statusCode = HttpStatus.CREATED.value(),
                    message = "Registration successful",
                    data = result
                )
            )
    }

    @PostMapping("/login/otp")
    fun sendOtpForLogin(
        @Valid
        @RequestBody body: LoginOtpRequest
    ): ResponseEntity<CustomApiResponse<Unit>> {
        userAuthService.sendOtpForLogin(body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "OTP sent to your Email"
            )
        )
    }

    @PostMapping("/login")
    fun login(
        @Valid
        @RequestBody body: LoginRequest
    ): ResponseEntity<CustomApiResponse<SessionResponse>> {
        val result = userAuthService.verifyOtpAndLogin(body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Login successful",
                data = result
            )
        )
    }


    @PostMapping("/password/reset/otp")
    fun sendOtpForResetPassword(
        @Valid
        @RequestBody body: PaswrdResetOtpRequest
    ): ResponseEntity<CustomApiResponse<Unit>> {
        userAuthService.sendOtpForResetPassword(body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "OTP sent to your Email"
            )
        )
    }

    @PostMapping("/password/reset")
    fun resetPassword(
        @Valid
        @RequestBody body: PasswordResetRequest
    ): ResponseEntity<CustomApiResponse<Unit>> {
        userAuthService.validateOtpAndResetPassword(body)
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(
                CustomApiResponse(
                    statusCode = HttpStatus.ACCEPTED.value(),
                    message = "Password changed successfully."
                )
            )
    }
}