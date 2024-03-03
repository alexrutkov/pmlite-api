package ru.pmlite.api.teams.dto

import ru.pmlite.api.tasks.domain.UserTeamRole
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

data class TeamUserRoleDto(
  val teamId: TeamId,
  val userId: UserId,
  val role: UserTeamRole,
  val agreementId: AgreementId
)
