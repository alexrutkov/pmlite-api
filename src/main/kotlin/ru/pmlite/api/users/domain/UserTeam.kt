package ru.pmlite.api.users.domain

import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TeamId
import java.time.Instant

data class UserTeam(
    val agreementId: AgreementId,
    val taskId: TeamId,
    val userId: Long,
    val name: String,
    val role: UserTeamRole,
    val createdAt: Instant
) {
    val id get() = agreementId.id
}
