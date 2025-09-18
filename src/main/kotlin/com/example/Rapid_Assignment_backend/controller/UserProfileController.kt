package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.common.CustomApiResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileUpdateRequest
import com.example.Rapid_Assignment_backend.services.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/users/profile")
class UserProfileController(
    private val profileService: UserProfileService
) {
    val okStausCode = HttpStatus.OK.value()

    @GetMapping
    fun fetchUserProfile(): ResponseEntity<CustomApiResponse<UserProfileResponse>> {
        val result = profileService.fetchUserProfile()
        return ResponseEntity.ok(
            CustomApiResponse(
                statusCode = okStausCode,
                message = "Profile fetched",
                data = result
            )
        )
    }

    @PutMapping("/update")
    fun updateUserProfile(
        @RequestBody body: UserProfileUpdateRequest
    ): ResponseEntity<CustomApiResponse<Unit>?> {
        profileService.updateUserProfile(request = body)
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(
                CustomApiResponse(
                    statusCode = HttpStatus.ACCEPTED.value(),
                    message = "Profile update successfully"
                )
            )
    }

    @PatchMapping("/picture")
    fun uploadUserProfilePic(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<CustomApiResponse<Unit>> {
        profileService.uploadUserProfilePic(file = file)
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(
                CustomApiResponse(
                    statusCode = okStausCode,
                    message = "Profile pic update successfully"
                )
            )
    }
}