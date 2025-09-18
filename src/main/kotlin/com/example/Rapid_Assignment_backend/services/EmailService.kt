package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.dto.common.MailRequest
import com.example.Rapid_Assignment_backend.utils.TemplateLoader
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val mailSender: JavaMailSender,

    @Value("\${spring.mail.username}")
    private val springMailUserName: String,
) {
    fun sendMail(request: MailRequest) {
        val message = SimpleMailMessage()
        message.from = springMailUserName
        message.setTo(request.to)
        message.subject = request.subject
        message.text = request.body
        mailSender.send(message)
    }

    fun sendHtmlBodyMail(request: MailRequest) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        helper.setFrom(springMailUserName)
        helper.setTo(request.to)
        helper.setSubject(request.subject)
        helper.setText(request.body, true)

        mailSender.send(message)
    }

    fun sendOtpMail(to: String, userName: String, otp: String, purpose: String) {
        val subject = "Your Rapid Assignment $purpose Verification Code"
        val htmlBody = createOtpEmailBody(userName, otp, purpose)
        val mailRequest = MailRequest(to, subject, htmlBody)
        sendHtmlBodyMail(mailRequest)
    }

    private fun createOtpEmailBody(userName: String, otp: String, purpose: String): String {
        val purposeText = when (purpose.lowercase()) {
            "login" -> "Your One-Time Password (OTP) for login is below. Please use it to access your account."
            "registration" -> "Thank you for choosing Rapid Assignment. Please use the following One-Time Password (OTP) to verify your account."
            "forgot password" -> "We have received a request to reset your password. Please use the following One-Time Password (OTP) to continue the process."
            else -> "Your One-Time Password (OTP) is below. Please use it to complete your action."
        }

        val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM, yyyy"))
        val template = TemplateLoader.loadTemplate("otp_template.html")
        return template
            .replace("{{formattedDate}}", formattedDate)
            .replace("{{userName}}", userName)
            .replace("{{purposeText}}", purposeText)
            .replace("{{otp}}", otp)
    }


    fun sendPasswordChangedMail(to: String, userName: String) {
        val subject = "Account Password Changed. Rapid Assignment."
        val htmlBody = createPasswordChangedEmailBody(userName)
        val mailRequest = MailRequest(to, subject, htmlBody)
        sendHtmlBodyMail(mailRequest)
    }

    private fun createPasswordChangedEmailBody(userName: String): String {
        val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM, yyyy"))
        val template = TemplateLoader.loadTemplate("password_changed.html")
        return template
            .replace("{{formattedDate}}", formattedDate)
            .replace("{{userName}}", userName)
    }
}