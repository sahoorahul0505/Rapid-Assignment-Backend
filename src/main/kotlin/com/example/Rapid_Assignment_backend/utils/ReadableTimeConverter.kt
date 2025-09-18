package com.example.Rapid_Assignment_backend.utils

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Instant.toReadableTime(): String {
    val now = Instant.now()
    val duration = Duration.between(this, now)

    return when {
        duration.toMinutes() < 1 -> "just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ego."
        duration.toHours() < 24 -> "${duration.toHours()} hours ego."
        duration.toDays() < 1L -> "yesterday"
        else -> DateTimeFormatter.ofPattern("dd MM yyyy, HH:MM")
            .withZone(ZoneId.systemDefault())
            .format(this)
    }
}