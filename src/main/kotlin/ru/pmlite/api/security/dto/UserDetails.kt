package ru.pmlite.api.security.dto

import ru.pmlite.api.values.UserId
import java.time.Instant

data class UserDetails(
    val userId: UserId,
    val name: String,
    val email: String,
    val updatedAt: Instant
)
