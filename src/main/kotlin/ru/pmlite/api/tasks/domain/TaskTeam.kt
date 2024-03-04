package ru.pmlite.api.tasks.domain

import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId
import java.time.Instant

data class TaskTeam(
    val agreementId: AgreementId,
    val teamId: TeamId,
    val taskId: TaskId,
    val name: String,
    val createdAt: Instant
) {
    val id get() = agreementId.id
}
