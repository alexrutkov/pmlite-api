package ru.pmlite.api.account.dto

import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.values.UserId

data class AccountDetails(
    val id: UserId,
    val name: String,
    val roles: List<UserRole> = emptyList()
)
