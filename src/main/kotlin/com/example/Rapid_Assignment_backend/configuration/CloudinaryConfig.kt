package com.example.Rapid_Assignment_backend.configuration

import com.cloudinary.Cloudinary
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class CloudinaryConfig(

    @Value("\${cloudinary.cloud.name}")
    private val cloudName : String,

    @Value("\${cloudinary.api.key}")
    private val apiKey : String,

    @Value("\${cloudinary.api.secret}")
    private val apiSecret : String
) {

    @Bean
    fun cloudinary() : Cloudinary {
        return Cloudinary(
            mapOf(
                "cloud_name" to cloudName,
                "api_key" to apiKey,
                "api_secret" to apiSecret
            )
        )
    }
}















