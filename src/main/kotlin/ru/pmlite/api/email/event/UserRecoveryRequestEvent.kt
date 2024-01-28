package ru.pmlite.api.email.event

import ru.pmlite.api.values.UserId

data class UserRecoveryRequestEvent(
    val userId: UserId,
    val email: String,
    val name: String
)
