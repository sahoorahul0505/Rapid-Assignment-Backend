package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.SessionResponse
import com.example.Rapid_Assignment_backend.dto.teacher.RegisterOtpRequest
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherRegisterRequest
import com.example.Rapid_Assignment_backend.services.TeacherAuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/teacher")
class TeacherAuthController(
    private val teacherAuthService: TeacherAuthService
) {
    val okStatus = HttpStatus.OK.value()

    // ------------------------------ REGISTRATION -------------------------------------------//

    @PostMapping("/send-registration-otp")
    fun sendOtpForRegister(@RequestBody body: RegisterOtpRequest): ResponseEntity<ApiResponse<Unit>?> {
        teacherAuthService.sendOtpForRegistrationTeacher(body)
        return ResponseEntity.ok(ApiResponse(statusCode = okStatus, message = "OTP sent to your Email"))
    }

    @PostMapping("/register")
    fun registerTeacher(
        @RequestBody body: TeacherRegisterRequest,
        @RequestParam otp: String
    ): ResponseEntity<ApiResponse<SessionResponse>> {
        val result = teacherAuthService.verifyOtpAndRegisterTeacher(request = body, otp = otp)
        return ResponseEntity.ok(ApiResponse(statusCode = okStatus, message = "Registration successful", data = result))
    }

    // -------------------------------- LOGIN -----------------------------------//

    @PostMapping("/send-login-otp")
    fun sendOtpForLoginTeacher(@RequestBody body: CommonLoginRequest): ResponseEntity<ApiResponse<Unit>?> {
        teacherAuthService.sendOtpForLogin(body)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStatus,
                message = "Otp sent to your Email"
            )
        )
    }

    @PostMapping("/login")
    fun loginTeacher(
        @RequestBody body: CommonLoginRequest,
        @RequestParam otp: String
    ): ResponseEntity<ApiResponse<SessionResponse>> {
        val result = teacherAuthService.verifyOtpAndLogin(body, otp)
        return ResponseEntity.ok(ApiResponse(statusCode = okStatus, message = "Login successful", data = result))
    }
}