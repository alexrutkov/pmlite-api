package ru.pmlite.api.email.dto

import ru.pmlite.api.security.domain.UserTokenType
import ru.pmlite.api.values.UserId
import java.time.LocalDateTime

data class CreateUserTokenCommand(
    val userId: UserId,
    val type: UserTokenType,
    val expiredAt: LocalDateTime
)
