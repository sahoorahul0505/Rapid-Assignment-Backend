package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.CustomApiResponse
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherProfileResponse
import com.example.Rapid_Assignment_backend.dto.teacher.TeacherProfileUpdateRequest
import com.example.Rapid_Assignment_backend.services.TeacherProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/teachers/profile")
class TeacherProfileController(
    private val teacherProfileService: TeacherProfileService
) {

    val okStatusCode = HttpStatus.OK.value()

    @GetMapping
    fun fetchTeacherProfile(): ResponseEntity<CustomApiResponse<TeacherProfileResponse>> {
        val result = teacherProfileService.fetchTeacherProfile()
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Profile fetched successfully",
                data = result
            )
        )
    }

    @PutMapping("/update")
    fun updateTeacherProfile(@RequestBody body: TeacherProfileUpdateRequest): ResponseEntity<CustomApiResponse<Unit>> {
        teacherProfileService.updateTeacherProfile(body)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Profile updated successfully"
            )
        )
    }

    @PatchMapping("/picture")
    fun uploadTeacherProfilePic(@RequestParam("file") file: MultipartFile): ResponseEntity<CustomApiResponse<Unit>> {
        teacherProfileService.uploadTeacherProfilePic(file)
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStatusCode,
                message = "Profile picture uploaded successfully"
            )
        )
    }
}