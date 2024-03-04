package ru.pmlite.api.teams.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.account.domains.AccountEventType
import ru.pmlite.api.account.events.AccountEvent
import ru.pmlite.api.agreements.domains.Decision
import ru.pmlite.api.agreements.dto.DecisionCommand
import ru.pmlite.api.agreements.services.DecisionService
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.teams.domain.TeamUser
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.teams.repositories.TeamUserRepository
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@Service
class TeamUserService(
    private val teamUserRepository: TeamUserRepository,
    private val securityService: SecurityService,
    private val decisionService: DecisionService,
    private val publisher: ApplicationEventPublisher
) {


    fun getUsers(teamId: TeamId, pageable: Pageable): List<TeamUser> {
        return teamUserRepository.getUserRelation(teamId, pageable)
    }

    private fun isAllowedTeamOperation(teamId: TeamId): Boolean {
        return teamUserRepository.getTeamUser(teamId, securityService.userId).role == UserTeamRole.OWNER
    }

    @Transactional
    fun cancelUser(teamId: TeamId, userId: UserId) {
        val userTeam = teamUserRepository.getTeamUser(teamId, userId)
        if (isAllowedTeamOperation(teamId)) {
            decisionService.decide(DecisionCommand(userTeam.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TEAM_UPDATED))
        }
    }
    @Transactional
    fun cancelTeam(teamId: TeamId) {
        val userTask = teamUserRepository.getTeamUser(teamId, securityService.userId)
        if (userTask.role != UserTeamRole.OWNER) {
            decisionService.decide(DecisionCommand(userTask.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TEAM_UPDATED))
        }
    }
}
