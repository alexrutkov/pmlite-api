package ru.pmlite.api.security.dto

import ru.pmlite.api.values.UserId

data class UserAuthenticatedDetails(
    val email: String,
    val password: String,
    val userId: UserId
)
