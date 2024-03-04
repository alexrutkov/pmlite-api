package ru.pmlite.api.teams.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.teams.services.TeamTaskService
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@RequestMapping("/api/teams")
@RestController
class TeamTasksController(
    private val service: TeamTaskService
) {

    @GetMapping("{id}/tasks")
    fun getTasks(@PathVariable id: TeamId, pageable: Pageable) = service.getTasks(id, pageable)

    @DeleteMapping("{id}/tasks/{taskId}")
    fun cancelTask(
        @PathVariable id: TeamId,
        @PathVariable taskId: TaskId
    ) = service.cancelTask(id, taskId)
}
