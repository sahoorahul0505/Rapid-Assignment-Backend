package com.example.Rapid_Assignment_backend.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val sessionAuthInterceptor: SessionAuthInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(sessionAuthInterceptor)
            .addPathPatterns("/**") // protect all
            .excludePathPatterns("/auth/**") // skip for auth
    }
}