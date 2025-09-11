package com.example.Rapid_Assignment_backend.controller.user

import com.example.Rapid_Assignment_backend.dto.user.UserProfileResponse
import com.example.Rapid_Assignment_backend.dto.user.UserProfileUpdateRequest
import com.example.Rapid_Assignment_backend.services.user.UserProfileService
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

    @PostMapping
    fun profile(): ResponseEntity<UserProfileResponse> {
        val response = profileService.profile()
        return ResponseEntity.ok(response)
    }

    @PutMapping("/update")
    fun updateProfile(
        @RequestBody body: UserProfileUpdateRequest
    ): ResponseEntity<UserProfileResponse> {
        val response = profileService.updateProfile(request = body)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/upload-profile-pic")
    fun uploadProfilePic(
        @RequestParam("file") file: MultipartFile
    ) : ResponseEntity<UserProfileResponse> {
        val response = profileService.uploadProfilePic(file = file)
        return ResponseEntity.ok(response)
    }
}