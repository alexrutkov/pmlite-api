package ru.pmlite.api.account.dto

import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.tasks.dto.TaskUserRoleDto
import ru.pmlite.api.values.UserId

data class AccountDetails(
    val id: Long,
    val name: String,
    val roles: List<UserRole> = emptyList(),
    val taskRoles: List<AccountTaskRole> = emptyList()
)
