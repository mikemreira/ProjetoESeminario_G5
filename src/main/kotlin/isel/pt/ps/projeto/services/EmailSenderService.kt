package isel.pt.ps.projeto.services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSenderService(
    private val emailSender: JavaMailSender
) {
    fun sendEmail(toEmail : String, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.from = "registosdeacesso@gmail.com"
        message.setTo(toEmail)
        message.text = "Este Link irá redirecioná-lo $body"
        message.subject = subject
        emailSender.send(message)

        println("EMAIL SENT ... ")
    }
}