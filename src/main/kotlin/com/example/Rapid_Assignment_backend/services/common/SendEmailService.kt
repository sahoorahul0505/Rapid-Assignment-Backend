package com.example.Rapid_Assignment_backend.services.common

import com.example.Rapid_Assignment_backend.dto.common.MailRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class SendEmailService(
    private val mailSender : JavaMailSender,

    @Value("\${spring.mail.username}")
    private val springMailUserName : String
) {
    fun sendMail(request: MailRequest) {
        val message = SimpleMailMessage()
        message.from = springMailUserName
        message.setTo(request.to)
        message.subject = request.subject
        message.text = request.body
        mailSender.send(message)
    }
}