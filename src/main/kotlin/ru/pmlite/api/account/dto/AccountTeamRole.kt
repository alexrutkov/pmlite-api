package ru.pmlite.api.account.dto

import ru.pmlite.api.tasks.domain.UserTeamRole
import ru.pmlite.api.values.TeamId

data class AccountTeamRole(
  val teamId: TeamId,
  val role: UserTeamRole
)
