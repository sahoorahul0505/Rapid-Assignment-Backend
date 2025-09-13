package com.example.Rapid_Assignment_backend.services

import com.example.Rapid_Assignment_backend.dto.common.MailRequest
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SendEmailService(
    private val mailSender : JavaMailSender,

    @Value("\${spring.mail.username}")
    private val springMailUserName : String,
) {
    fun sendMail(request: MailRequest) {
        val message = SimpleMailMessage()
        message.from = springMailUserName
        message.setTo(request.to)
        message.subject = request.subject
        message.text = request.body
        mailSender.send(message)
    }

    fun sendHtmlBodyMail(request: MailRequest){
        val message : MimeMessage = mailSender.createMimeMessage()
        val helper  = MimeMessageHelper(message, "utf-8")
        helper.setFrom(springMailUserName)
        helper.setTo(request.to)
        helper.setSubject(request.subject)
        helper.setText(request.body, true)

        mailSender.send(message)
    }


    /**
     * Sends an OTP email using a dynamic HTML template.
     * @param to The recipient's email address.
     * @param userName The user's name to personalize the email.
     * @param otp The one-time password to include in the email.
     * @param purpose The purpose of the OTP (e.g., "Login", "Registration", "Forgot Password").
     */
    fun sendOtpMailHtmlBody(to: String, userName: String, otp: String, purpose: String) {
        val subject = "Your Rapid Assignment $purpose Verification Code"
        val htmlBody = createOtpEmailBody(userName, otp, purpose)
        val mailRequest = MailRequest(to, subject, htmlBody)
        sendHtmlBodyMail(mailRequest)
    }

    /**
     * Generates the HTML body for the OTP email with dynamic content.
     * This function is private because it's only used internally by the service.
     */
    private fun createOtpEmailBody(userName: String, otp: String, purpose: String): String {
        val purposeText = when (purpose.lowercase()) {
            "login" -> "Your One-Time Password (OTP) for login is below. Please use it to access your account."
            "registration" -> "Thank you for choosing Rapid Assignment. Please use the following One-Time Password (OTP) to verify your account."
            "forgot password" -> "We have received a request to reset your password. Please use the following One-Time Password (OTP) to continue the process."
            else -> "Your One-Time Password (OTP) is below. Please use it to complete your action."
        }

        val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM, yyyy"))

        // This is the full HTML template with dynamic fields
        return """
           <!DOCTYPE html>
            <html lang="en">

            <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <meta http-equiv="X-UA-Compatible" content="ie=edge" />
            <title>OTP from Rapid Assignment</title>

            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet" />
        </head>

        <body style="
                margin: 0;
                font-family: 'Poppins', sans-serif;
                background: #ffffff;
                font-size: 14px;
                ">
        <div style="
                  max-width: 680px;
                  margin: 0 auto;
                  padding: 45px 30px 60px;
                  background: #f4f7ff;
                  background-image: url(https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661497957196_595865/email-template-background-banner);
                  background-repeat: no-repeat;
                  background-size: 800px 452px;
                  background-position: top center;
                  font-size: 14px;
                  color: #434343;
                ">
        <header>
            <table style="width: 100%;">
                <tbody>
                    <tr style="height: 0;">
                        <td>
                            <img alt="Rapid Assignment Logo" src="https://res.cloudinary.com/dnrndymh0/image/upload/v1757666950/rapid_assign_logo_logo_hckz8w.png" height="64px" width="192px" />
                        </td>
                        <td style="text-align: right;">
                            <span style="font-size: 16px; line-height: 30px; color: #ffffff;">$formattedDate</span>
                        </td>
                    </tr>
                </tbody>
            </table>
        </header>

        <main>
            <div style="
                      margin: 0;
                      margin-top: 70px;
                      padding: 92px 30px 115px;
                      background: #ffffff;
                      border-radius: 30px;
                      text-align: center;
                    ">
                <div style="width: 100%; max-width: 489px; margin: 0 auto;">
                    <h1 style="
                          margin: 0;
                          font-size: 24px;
                          font-weight: 500;
                          color: #1f1f1f;
                        ">
                        Your OTP
                    </h1>
                    <p style="
                          margin: 0;
                          margin-top: 17px;
                          font-size: 16px;
                          font-weight: 500;
                        ">
                        Hey $userName,
                    </p>
                    <p style="
                          margin: 0;
                          margin-top: 17px;
                          font-weight: 500;
                          letter-spacing: 0.56px;
                        ">
                        $purposeText OTP is valid for
                        <span style="font-weight: 600; color: #1f1f1f;">5 minutes</span>.
                        Do not share this code with anyone, including Rapid Assignment
                        employees.
                    </p>
                    <p style="
                          margin: 0;
                          margin-top: 56px;
                          font-size: 32px;
                          font-weight: 600;
                          letter-spacing: 8px;
                          color: #ff6600;
                        ">
                        $otp
                    </p>
                </div>
            </div>

            <p style="
                      max-width: 400px;
                      margin: 0 auto;
                      margin-top: 90px;
                      text-align: center;
                      font-weight: 500;
                      color: #8c8c8c;
                    ">
                Need help? Contact us at
                <a href="mailto:support@rapidassignment.com"
                    style="color: #499fb6; text-decoration: none;">suport.rapidassignment@gmail.com</a>
            </p>
        </main>

        <footer style="
                    width: 100%;
                    max-width: 490px;
                    margin: 20px auto 0;
                    text-align: center;
                    border-top: 1px solid #e6ebf1;
                  ">
            <p style="
                      margin: 0;
                      margin-top: 40px;
                      font-size: 16px;
                      font-weight: 600;
                      color: #434343;
                    ">
                Rapid Assignment
            </p>
            <p style="margin: 0; margin-top: 8px; color: #434343;">
                Jagannath Developers 305 ,Bhubaneswar, Odisha, India
            </p>
            <div style="margin: 0; margin-top: 16px;">
                <a href="#" target="_blank" style="display: inline-block;">
                    <img width="36px" alt="Facebook"
                        src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661502815169_682499/email-template-icon-facebook" />
                </a>
                <a href="#" target="_blank" style="display: inline-block; margin-left: 8px;">
                    <img width="36px" alt="Instagram"
                        src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661504218208_684135/email-template-icon-instagram" /></a>
                <a href="#" target="_blank" style="display: inline-block; margin-left: 8px;">
                    <img width="36px" alt="Twitter"
                        src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661503043040_372004/email-template-icon-twitter" />
                </a>
                <a href="#" target="_blank" style="display: inline-block; margin-left: 8px;">
                    <img width="36px" alt="Youtube"
                        src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661503195931_210869/email-template-icon-youtube" /></a>
            </div>
            <p style="margin: 0; margin-top: 16px; color: #434343;">
                Copyright © 2025 Rapid Assignment. All rights reserved.
            </p>
        </footer>
    </div>
</body>

</html>
        """.trimIndent()
    }


    fun sendPasswordMailHtmlBody(to: String, userName: String, password: String) {
        val subject = "Your Rapid Assignment Account Password"
        val htmlBody = createPasswordEmailBody(userName, password)
        val mailRequest = MailRequest(to, subject, htmlBody)
        sendHtmlBodyMail(mailRequest)
    }

    /**
     * Generates the HTML body for the password notification email with dynamic content.
     */
    private fun createPasswordEmailBody(userName: String, password: String): String {
        val formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM, yyyy"))

        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <meta http-equiv="X-UA-Compatible" content="ie=edge" />
            <title>Password from Rapid Assignment</title>
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet" />
        </head>

        <body style="
                margin: 0;
                font-family: 'Poppins', sans-serif;
                background: #ffffff;
                font-size: 14px;
            ">
            <div style="
                    max-width: 680px;
                    margin: 0 auto;
                    padding: 45px 30px 60px;
                    background: #f4f7ff;
                    background-image: url(https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661497957196_595865/email-template-background-banner);
                    background-repeat: no-repeat;
                    background-size: 800px 452px;
                    background-position: top center;
                    font-size: 14px;
                    color: #434343;
                ">
                <header>
                    <table style="width: 100%;">
                        <tbody>
                            <tr style="height: 0;">
                                <td>
                                    <img alt="Rapid Assignment Logo" src="https://res.cloudinary.com/dnrndymh0/image/upload/v1757666950/rapid_assign_logo_logo_hckz8w.png" height="64px" width="192px" />
                                </td>
                                <td style="text-align: right;">
                                    <span style="font-size: 16px; line-height: 24px; color: #ffffff;">$formattedDate</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </header>

                <main>
                    <div style="
                        margin: 0;
                        margin-top: 70px;
                        padding: 92px 30px 115px;
                        background: #ffffff;
                        border-radius: 30px;
                        text-align: center;
                    ">
                        <div style="width: 100%; max-width: 489px; margin: 0 auto;">
                            <h1 style="
                                margin: 0;
                                font-size: 24px;
                                font-weight: 500;
                                color: #1f1f1f;
                            ">
                                Your Account Password
                            </h1>
                            <p style="
                                margin: 0;
                                margin-top: 17px;
                                font-size: 16px;
                                font-weight: 500;
                            ">
                                Hello $userName,
                            </p>
                            <p style="
                                margin: 0;
                                margin-top: 17px;
                                font-weight: 500;
                                letter-spacing: 0.56px;
                            ">
                                Below is your password to access your account.<br>
                                <span style="font-weight: 600; color: #1f1f1f;">
                                    Please keep it confidential and do not share your password with anyone.
                                </span>
                            </p>
                            <p style="
                                margin: 0;
                                margin-top: 56px;
                                font-size: 16px;
                                font-weight: 600;
                                letter-spacing:1px;
                                color: #ff6600;
                            ">
                                $password
                            </p>
                        </div>
                    </div>

                    <p style="
                        max-width: 400px;
                        margin: 0 auto;
                        margin-top: 90px;
                        text-align: center;
                        font-weight: 500;
                        color: #8c8c8c;
                    ">
                        Need help? Contact us at
                        <a href="mailto:support@rapidassignment.com"
                            style="color: #499fb6; text-decoration: none;">support.rapidassignment@gmail.com</a>
                    </p>
                </main>

                <footer style="
                        width: 100%;
                        max-width: 490px;
                        margin: 20px auto 0;
                        text-align: center;
                        border-top: 1px solid #e6ebf1;
                    ">
                    <p style="
                            margin: 0;
                            margin-top: 40px;
                            font-size: 16px;
                            font-weight: 600;
                            color: #434343;
                        ">
                        Rapid Assignment
                    </p>
                    <p style="margin: 0; margin-top: 8px; color: #434343;">
                        Jagannath Developers 305 ,Bhubaneswar, Odisha, India
                    </p>
                    <div style="margin: 0; margin-top: 16px;">
                        <a href="#" target="_blank" style="display: inline-block;">
                            <img width="36px" alt="Facebook"
                                src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661502815169_682499/email-template-icon-facebook" />
                        </a>
                        <a href="#" target="_blank" style="display: inline-block; margin-left: 8px;">
                            <img width="36px" alt="Instagram"
                                src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661504218208_684135/email-template-icon-instagram" /></a>
                        <a href="#" target="_blank" style="display: inline-block; margin-left: 8px;">
                            <img width="36px" alt="Twitter"
                                src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661503043040_372004/email-template-icon-twitter" />
                        </a>
                        <a href="#" target="_blank" style="display: inline-block; margin-left: 8px;">
                            <img width="36px" alt="Youtube"
                                src="https://archisketch-resources.s3.ap-northeast-2.amazonaws.com/vrstyler/1661503195931_210869/email-template-icon-youtube" /></a>
                    </div>
                    <p style="margin: 0; margin-top: 16px; color: #434343;">
                        Copyright © 2025 Rapid Assignment. All rights reserved.
                    </p>
                </footer>
            </div>
        </body>

        </html>
    """.trimIndent()
    }

}