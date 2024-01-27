package ru.pmlite.api.email.services

import mu.KotlinLogging
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import ru.pmlite.api.email.dto.SendEmailCommand

private val logger = KotlinLogging.logger {}
@Service
class EmailService(
    private val mailSender: JavaMailSenderImpl
) {


    fun sendEmail(command: SendEmailCommand) {
        val mimeMessage = mailSender.createMimeMessage()
            .apply {
                setText(command.message, "utf-8")
                MimeMessageHelper(this, false, "utf-8")
                    .apply {
                        setFrom(mailSender.username!!)
                        setTo(command.email)
                        setSubject(command.subject)
                    }
            }
        runCatching { mailSender.send(mimeMessage) }
            .onFailure {  ex ->
                logger.error("[EMAIL] Сообщение не отправлено для ${command.email}, ${ex.localizedMessage}", ex)
            }
            .getOrThrow()
    }
}
