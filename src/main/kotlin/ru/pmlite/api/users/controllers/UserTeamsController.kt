package ru.pmlite.api.users.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.users.services.UserTeamService
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@RequestMapping("/api/users")
@RestController
class UserTeamsController(
    private val service: UserTeamService
) {

    @GetMapping("{id}/teams")
    fun getUserTasks(@PathVariable id: Long, pageable: Pageable) = service.getTeams(UserId(id), pageable)

    @DeleteMapping("teams/{teamId}")
    fun cancelTask(@PathVariable teamId: TeamId) = service.cancelTeam(teamId)
}
