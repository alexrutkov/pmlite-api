package ru.pmlite.api.email.services

import org.springframework.integration.annotation.MessagingGateway
import ru.pmlite.api.security.dto.ValidateEmailCommand

@MessagingGateway(defaultRequestChannel = "emailNotificationChannel")
interface EmailService {
    fun confirmEmail(command: ValidateEmailCommand)
}
