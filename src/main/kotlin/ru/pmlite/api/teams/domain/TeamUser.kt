package ru.pmlite.api.teams.domain

import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TeamId
import java.time.Instant

data class TeamUser(
    val agreementId: AgreementId,
    val teamId: TeamId,
    val user: UserSummary,
    val role: UserTeamRole,
    val createdAt: Instant
) {
    val id get() = agreementId.id
}
