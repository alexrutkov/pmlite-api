package ru.pmlite.api.users.services

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
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.users.domain.UserTeam
import ru.pmlite.api.users.repositories.UserTeamRepository
import ru.pmlite.api.values.TeamId
import ru.pmlite.api.values.UserId

@Service
class UserTeamService(
    private val userTaskRepository: UserTeamRepository,
    private val securityService: SecurityService,
    private val decisionService: DecisionService,
    private val publisher: ApplicationEventPublisher
) {


    fun getTeams(userId: UserId, pageable: Pageable): List<UserTeam> {
        return userTaskRepository.getTeams(userId, pageable)
    }


    @Transactional
    fun cancelTeam(teamId: TeamId) {
        val userTask = userTaskRepository.getUserTeam(securityService.userId, teamId)
        if (userTask.role != UserTeamRole.OWNER) {
            decisionService.decide(DecisionCommand(userTask.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TEAM_UPDATED))
        }
    }
}
