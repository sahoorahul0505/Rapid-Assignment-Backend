package com.example.Rapid_Assignment_backend.utils

import org.springframework.core.io.ClassPathResource

object TemplateLoader {

    fun loadTemplate(fileName: String): String {
        val resource = ClassPathResource("templates/$fileName")
        return resource.inputStream.bufferedReader().use { it.readText() }
    }

}