package ru.pmlite.api.teams.controllers

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.teams.dto.CreateTeamCommand
import ru.pmlite.api.teams.dto.UpdateTeamCommand
import ru.pmlite.api.teams.services.TeamService
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@RequestMapping("/api/teams")
@RestController
class TeamController(
  private val service: TeamService
) {

  @PostMapping
  fun createTask(@Valid @RequestBody command: CreateTeamCommand) = service.createTeam(command)

  @PutMapping("{id}")
  fun updateTask(
    @PathVariable id: TeamId,
    @Valid @RequestBody command: UpdateTeamCommand
  ) = service.updateTeam(id, command)

  @PostMapping("{id}/join")
  fun joinToTeam(@PathVariable id: TeamId) = service.joinToTeam(id)

  @PostMapping("{id}/joinToTask/{taskId}")
  fun joinTeamToTask(
    @PathVariable id: TeamId,
    @PathVariable taskId: TaskId
  ) = service.joinTeamToTask(id, taskId)
}
