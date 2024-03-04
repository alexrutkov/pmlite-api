package ru.pmlite.api.teams.domain

import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId
import java.time.Instant

data class TeamTask(
    val agreementId: AgreementId,
    val teamId: TeamId,
    val taskId: TaskId,
    val name: String,
    val createdAt: Instant
) {
    val id get() = agreementId.id
}
