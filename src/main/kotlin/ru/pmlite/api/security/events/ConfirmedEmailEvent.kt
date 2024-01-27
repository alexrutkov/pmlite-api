package ru.pmlite.api.security.events

import ru.pmlite.api.values.UserId

data class ConfirmedEmailEvent(
    val userId: UserId
)
