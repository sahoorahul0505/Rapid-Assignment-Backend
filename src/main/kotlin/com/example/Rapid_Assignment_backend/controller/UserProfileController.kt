package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.ApiResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileUpdateRequest
import com.example.Rapid_Assignment_backend.services.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/user/profile")
class UserProfileController(
    private val profileService: UserProfileService
) {
    val okStausCode = HttpStatus.OK.value()

    @PostMapping
    fun fetchUserProfile(): ResponseEntity<ApiResponse<UserProfileResponse>> {
        val result = profileService.fetchUserProfile()
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStausCode,
                message = "Profile fetched",
                data = result
            )
        )
    }

    @PutMapping("/update")
    fun updateProfile(
        @RequestBody body: UserProfileUpdateRequest,
        @RequestParam password : String
    ): ResponseEntity<ApiResponse<Unit>?> {
        val result = profileService.updateProfile(request = body, password = password)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStausCode,
                message = "Profile update successfully",
                data = result
            )
        )
    }

    @PostMapping("/upload-profile-pic")
    fun uploadProfilePic(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse<String>> {
        profileService.uploadProfilePic(file = file)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = okStausCode,
                message = "Profile pic update successfully"
            )
        )
    }
}