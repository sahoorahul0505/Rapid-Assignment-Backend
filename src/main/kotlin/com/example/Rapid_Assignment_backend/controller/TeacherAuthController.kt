package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.*
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherRegisterRequest
import com.example.Rapid_Assignment_backend.services.TeacherAuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/teachers/auth")
class TeacherAuthController(
    private val teacherAuthService: TeacherAuthService
) {
    val okStatus = HttpStatus.OK.value()

    // ------------------------------ REGISTRATION -------------------------------------------//

    @PostMapping("/register/otp")
    fun sendOtpForRegisterTeacher(
        @Valid
        @RequestBody body: RegisterOtpRequest
    ): ResponseEntity<CustomApiResponse<Unit>?> {
        teacherAuthService.sendOtpForRegistrationTeacher(body)
        return ResponseEntity.ok(CustomApiResponse(statusCode = okStatus, message = "OTP sent to your Email"))
    }

    @PostMapping("/register")
    fun registerTeacher(
        @Valid
        @RequestBody body: TeacherRegisterRequest
    ): ResponseEntity<CustomApiResponse<SessionResponse>> {
        val result = teacherAuthService.verifyOtpAndRegisterTeacher(body) // -> (TeacherAuthController.kt:37)
        return ResponseEntity.ok(CustomApiResponse(statusCode = okStatus, message = "Registration successful", data = result))
    }

    // -------------------------------- LOGIN -----------------------------------//

    @PostMapping("/login/otp")
    fun sendOtpForLoginTeacher(
        @Valid
        @RequestBody body: LoginOtpRequest
    ): ResponseEntity<CustomApiResponse<Unit>?> {
        teacherAuthService.sendOtpForLoginTeacher(body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatus,
                message = "Otp sent to your Email"
            )
        )
    }

    @PostMapping("/login")
    fun loginTeacher(
        @Valid
        @RequestBody body: LoginRequest
    ): ResponseEntity<CustomApiResponse<SessionResponse>> {
        val result = teacherAuthService.verifyOtpAndLoginTeacher(body)
        return ResponseEntity.ok(CustomApiResponse(statusCode = okStatus, message = "Login successful", data = result))
    }
}