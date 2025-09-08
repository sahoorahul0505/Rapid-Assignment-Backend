package com.example.Rapid_Assignment_backend.controller

import com.example.Rapid_Assignment_backend.dto.ProfileRequest
import com.example.Rapid_Assignment_backend.dto.ProfileResponse
import com.example.Rapid_Assignment_backend.dto.ProfileUpdateRequest
import com.example.Rapid_Assignment_backend.services.ProfileService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val profileService: ProfileService
) {

    @PostMapping
    fun profile(@RequestBody body: ProfileRequest): ResponseEntity<ProfileResponse> {
        val response = profileService.profile(body.token)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/update")
    fun updateProfile(
        @RequestBody body: ProfileUpdateRequest,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<ProfileResponse> {
        val token = httpServletRequest.getHeader("X-Session-Token") ?: return ResponseEntity.badRequest().build()
        val response = profileService.updateProfile(token = token, request = body)
        return ResponseEntity.ok(response)
    }
}