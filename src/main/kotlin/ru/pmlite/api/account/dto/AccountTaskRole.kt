package ru.pmlite.api.account.dto

import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.values.TaskId

data class AccountTaskRole(
    val taskId: TaskId,
    val role: UserTaskRole
)
