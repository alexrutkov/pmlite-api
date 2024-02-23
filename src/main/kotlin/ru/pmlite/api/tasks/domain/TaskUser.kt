package ru.pmlite.api.tasks.domain

import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import java.time.Instant

data class TaskUser(
    val agreementId: AgreementId,
    val taskId: TaskId,
    val user: UserSummary,
    val role: UserTaskRole,
    val createdAt: Instant
) {
    val id get() = agreementId.id
}
