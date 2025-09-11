package com.example.Rapid_Assignment_backend.controller.teacher

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginRequest
import com.example.Rapid_Assignment_backend.dto.common.CommonLoginResponse
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherRegisterRequest
import com.example.Rapid_Assignment_backend.services.teacher.TeacherAuthService
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

    // ------------------------------ REGISTRATION -------------------------------------------//

    @PostMapping("/send-registration-otp")
    fun sendAuthOtp(@RequestBody body: TeacherRegisterRequest): ResponseEntity<ApiResponse> {
        val response = teacherAuthService.sendOtpForRegistration(body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/register")
    fun register(@RequestBody body: TeacherRegisterRequest, @RequestParam otp: String): ResponseEntity<ApiResponse> {
        val response = teacherAuthService.verifyOtpAndRegister(request = body, otp = otp)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    // -------------------------------- LOGIN -----------------------------------//

    @PostMapping("/send-login-otp")
    fun sendOtpForLogin(@RequestBody body: CommonLoginRequest): ResponseEntity<ApiResponse> {
        val response = teacherAuthService.sendOtpForLogin(body)
        return ResponseEntity.ok(ApiResponse(success = true, message = response))
    }

    @PostMapping("/login")
    fun login(@RequestBody body: CommonLoginRequest, @RequestParam otp: String): ResponseEntity<CommonLoginResponse> {
        val response = teacherAuthService.verifyOtpAndLogin(body, otp)
        return ResponseEntity.ok(response)
    }
}