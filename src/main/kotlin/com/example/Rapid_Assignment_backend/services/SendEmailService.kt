package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.dto.MailRequest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class SendEmailService(
    private val mailSender : JavaMailSender
) {
    fun sendMail(request: MailRequest) {
        val message = SimpleMailMessage()
        message.from = "\${SPRING_MAIL_USERNAME}"
        message.setTo(request.to)
        message.subject = request.subject
        message.text = request.body
        mailSender.send(message)
    }
}