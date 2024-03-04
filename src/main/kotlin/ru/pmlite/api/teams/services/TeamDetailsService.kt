package ru.pmlite.api.teams.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.services.LikesService
import ru.pmlite.api.likes.services.StarsService
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.teams.dto.TeamDetails
import ru.pmlite.api.teams.dto.TeamSummary
import ru.pmlite.api.teams.repositories.SearchTeamRepository
import ru.pmlite.api.teams.repositories.TeamRepository
import ru.pmlite.api.values.TeamId

@Service
class TeamDetailsService(
  private val securityService: SecurityService,
  private val repository: TeamRepository,
  private val searchRepository: SearchTeamRepository,
  private val likesService: LikesService,
  private val starsService: StarsService
) {

  fun getTeam(teamId: TeamId): TeamDetails {
    return repository.getTeam(teamId)
  }

  fun getAllTeams(pageable: Pageable): List<TeamSummary> {
    return repository.getAllTeams(securityService.userId, pageable)
      .let(::addDetails)
  }

  fun getMyTeams(pageable: Pageable): List<TeamSummary>  {
    return repository.getMyTeams(securityService.userId, pageable)
      .let(::addDetails)
  }



  fun searchAllTeams(search: String, pageable: Pageable): List<TeamSummary> {
    return searchRepository.searchAllTeams(search, pageable)
      .let(::addDetails)
  }

  fun searchMyTeams(search: String, pageable: Pageable): List<TeamSummary> {
    return searchRepository.searchMyTeams(search, securityService.userId, pageable)
      .let(::addDetails)
  }

  private fun addDetails(entities: List<TeamSummary>): List<TeamSummary> {
    val myLikes =  likesService.getMyLikes(entities.map { it.id.entityId }, EntityType.TEAM)
    val myStars =  starsService.getMyStars(entities.map { it.id.entityId }, EntityType.TEAM)
    return entities.map {
      it.copy(
        isLiked = myLikes.contains(it.id.entityId),
        isStared = myStars.contains(it.id.entityId)
      )
    }
  }
}
