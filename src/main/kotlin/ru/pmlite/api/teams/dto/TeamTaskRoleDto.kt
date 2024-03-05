package ru.pmlite.api.teams.dto

import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

data class TeamTaskRoleDto(
  val taskId: TaskId,
  val teamId: TeamId,
  val agreementId: AgreementId
)
