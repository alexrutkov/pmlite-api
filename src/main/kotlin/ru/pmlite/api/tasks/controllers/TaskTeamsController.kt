package ru.pmlite.api.tasks.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.tasks.services.TaskTeamService
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@RequestMapping("/api/tasks")
@RestController
class TaskTeamsController(
    private val service: TaskTeamService
) {

    @GetMapping("{taskId}/teams")
    fun getTaskTeams(@PathVariable taskId: TaskId, pageable: Pageable) = service.getTaskTeams(taskId, pageable)

    @DeleteMapping("{taskId}/teams/{teamId}")
    fun cancelTaskTeam(
        @PathVariable teamId: TeamId,
        @PathVariable taskId: TaskId,
    ) = service.cancelTaskTeam(teamId, taskId)
}
