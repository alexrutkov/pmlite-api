package ru.pmlite.api.teams.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.teams.services.TeamDetailsService
import ru.pmlite.api.values.TeamId

@RequestMapping("/api/teams")
@RestController
class TeamDetailsController(
  private val service: TeamDetailsService
) {

  @GetMapping("all", params = ["search"])
  fun searchAllTeams(@RequestParam search: String, pageable: Pageable) = service.searchAllTeams(search, pageable)

  @GetMapping("my", params = ["search"])
  fun searchMyTeams(@RequestParam search: String, pageable: Pageable) = service.searchMyTeams(search, pageable)
  @GetMapping("all")
  fun getAllTeams(pageable: Pageable) = service.getAllTeams(pageable)

  @GetMapping("my")
  fun getMyTeams(pageable: Pageable) = service.getMyTeams(pageable)

  @GetMapping("{id}")
  fun getTeam(@PathVariable id: TeamId) = service.getTeam(id)
}
