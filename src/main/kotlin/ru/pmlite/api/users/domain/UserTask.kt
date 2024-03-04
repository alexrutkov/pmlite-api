package ru.pmlite.api.users.domain

import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import java.time.Instant

data class UserTask(
    val agreementId: AgreementId,
    val taskId: TaskId,
    val userId: Long,
    val name: String,
    val role: UserTaskRole,
    val createdAt: Instant
) {
    val id get() = agreementId.id
}
