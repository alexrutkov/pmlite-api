package ru.pmlite.api.email.event

import ru.pmlite.api.values.UserId

data class UserCreatedEvent(
    val userId: UserId,
    val name: String,
    val email: String
)
