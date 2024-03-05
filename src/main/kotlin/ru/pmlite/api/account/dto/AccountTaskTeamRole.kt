package ru.pmlite.api.account.dto

import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

data class AccountTaskTeamRole(
    val taskId: TaskId,
    val teamId: TeamId,
)
