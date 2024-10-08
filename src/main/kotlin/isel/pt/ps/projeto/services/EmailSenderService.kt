package isel.pt.ps.projeto.services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

// A
@Service
class EmailSenderService(
    private val emailSender: JavaMailSender
) {
    fun sendEmail(toEmail : String, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.setTo(toEmail)
        message.text = "$body\nEste link irá redirecioná-lo: https://registo-acessos.azurewebsites.net/"
        message.subject = subject
        println(message)
        emailSender.send(message)

        println("EMAIL SENT ... ")
    }
}