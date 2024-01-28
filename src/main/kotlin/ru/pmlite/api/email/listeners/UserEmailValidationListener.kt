package ru.pmlite.api.email.listeners

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.email.dto.CreateUserTokenCommand
import ru.pmlite.api.email.dto.SendEmailCommand
import ru.pmlite.api.email.event.UserCreatedEvent
import ru.pmlite.api.email.event.UserRecoveryRequestEvent
import ru.pmlite.api.email.services.EmailService
import ru.pmlite.api.security.domain.UserTokenType
import ru.pmlite.api.security.repositories.UserTokenRepository
import java.time.LocalDateTime

@Component
class UserEmailValidationListener(
    private val userTokenRepository: UserTokenRepository,
    private val emailService: EmailService,
    @Value("\${app.website.url}") private val websiteUrl: String
) {

    @Transactional @Async
    @EventListener(UserCreatedEvent::class)
    fun createValidateEmailMessage(event: UserCreatedEvent) {
        val token = CreateUserTokenCommand(
            event.userId,
            UserTokenType.EMAIL_VALIDATION,
            LocalDateTime.now().plusDays(7)
        ).let(userTokenRepository::createTokenBy)

        SendEmailCommand(
            event.email,
            """
                ${event.name}!
                Прошу подтвердить адрес электронной почты по ссылке:
                $websiteUrl/registration/confirm/${token}
            """.trimIndent(),
            "Проверка почтового ящика"
        ).also(emailService::sendEmail)
    }

    @Transactional @Async
    @EventListener(UserRecoveryRequestEvent::class)
    fun createValidateEmailMessage(event: UserRecoveryRequestEvent) {
        val token = CreateUserTokenCommand(
            event.userId, UserTokenType.RECOVERY, LocalDateTime.now().plusDays(7)
        ).let(userTokenRepository::createTokenBy)

        SendEmailCommand(
            event.email,
            """
                ${event.name}!
                Для восстановления доступа к личному кабинету пройдите по ссылке:
                $websiteUrl/registration/confirm/${token}
            """.trimIndent(),
            "Восстановление доступа к личному кабинету"
        ).also(emailService::sendEmail)
    }
}
