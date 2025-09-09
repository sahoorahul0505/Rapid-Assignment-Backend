package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.ProfileResponse
import com.example.Rapid_Assignment_backend.dto.ProfileUpdateRequest
import com.example.Rapid_Assignment_backend.services.ProfileService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val profileService: ProfileService
) {

    @PostMapping
    fun profile(httpServletRequest: HttpServletRequest): ResponseEntity<ProfileResponse> {
        val token = getHeader(httpServletRequest)
        val response = profileService.profile(token)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/update")
    fun updateProfile(
        @RequestBody body: ProfileUpdateRequest,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ProfileResponse> {
        val token = getHeader(httpServletRequest)
        val response = profileService.updateProfile(token = token, request = body)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/upload-profile-pic")
    fun uploadProfilePic(
        @RequestParam("file") file: MultipartFile,
        httpServletRequest: HttpServletRequest
    ) : ResponseEntity<ProfileResponse> {
        val token = getHeader(httpServletRequest)
        val response = profileService.uploadProfilePic(token = token, file = file)
        return ResponseEntity.ok(response)
    }
}