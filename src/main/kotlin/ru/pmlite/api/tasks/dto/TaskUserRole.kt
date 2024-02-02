package ru.pmlite.api.tasks.dto

import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

data class TaskUserRoleDto(
    val taskId: TaskId,
    val userId: UserId,
    val role: UserTaskRole,
    val agreementId: AgreementId
)
