package ru.pmlite.api.account.dto

import ru.pmlite.api.security.domain.UserRole

data class AccountDetails(
    val id: Long,
    val name: String,
    val roles: List<UserRole> = emptyList(),
    val taskRoles: List<AccountTaskRole> = emptyList(),
    val teamRoles: List<AccountTeamRole> = emptyList(),
)
