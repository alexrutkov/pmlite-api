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
import ru.pmlite.api.teams.domain.TeamTask
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.teams.repositories.TeamTaskRepository
import ru.pmlite.api.teams.repositories.TeamUserRepository
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@Service
class TeamTaskService(
    private val teamTaskRepository: TeamTaskRepository,
    private val teamUserRepository: TeamUserRepository,
    private val securityService: SecurityService,
    private val decisionService: DecisionService,
    private val publisher: ApplicationEventPublisher
) {


    fun getTasks(teamId: TeamId, pageable: Pageable): List<TeamTask> {
        return teamTaskRepository.getTaskRelation(teamId, pageable)
    }

    private fun isAllowedTeamOperation(teamId: TeamId): Boolean {
        return teamUserRepository.getTeamUser(teamId, securityService.userId).role == UserTeamRole.OWNER
    }

    @Transactional
    fun cancelTask(teamId: TeamId, taskId: TaskId) {
        val userTeam = teamTaskRepository.getTeamTask(teamId, taskId)
        if (isAllowedTeamOperation(teamId)) {
            decisionService.decide(DecisionCommand(userTeam.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TEAM_UPDATED))
        }
    }
}
