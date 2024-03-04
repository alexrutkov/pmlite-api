package ru.pmlite.api.teams.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.teams.services.TeamUserService
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@RequestMapping("/api/teams")
@RestController
class TeamUsersController(
    private val service: TeamUserService
) {

    @GetMapping("{id}/users")
    fun getUsers(@PathVariable id: TeamId, pageable: Pageable) = service.getUsers(id, pageable)

    @DeleteMapping("{id}/users/{userId}")
    fun cancelUser(
        @PathVariable id: TeamId,
        @PathVariable userId: Long
    ) = service.cancelUser(id, UserId(userId))
    @DeleteMapping("{id}")
    fun cancelTeam(@PathVariable id: TeamId) = service.cancelTeam(id)
}
