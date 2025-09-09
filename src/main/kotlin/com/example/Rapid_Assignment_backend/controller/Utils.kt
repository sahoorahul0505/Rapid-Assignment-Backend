package com.example.Rapid_Assignment_backend.controller

import jakarta.servlet.http.HttpServletRequest

fun getHeader(httpServletRequest: HttpServletRequest): String {
    val token = httpServletRequest.getHeader("X-session-token")
        ?: throw IllegalArgumentException("No token passed from Header")

    return token
}