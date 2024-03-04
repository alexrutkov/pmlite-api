package ru.pmlite.api.tasks.services

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
import ru.pmlite.api.tasks.domain.TaskTeam
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.tasks.repositories.TaskTeamRepository
import ru.pmlite.api.tasks.repositories.TaskUserRepository
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@Service
class TaskTeamService(
    private val taskTeamRepository: TaskTeamRepository,
    private val taskUserRepository: TaskUserRepository,
    private val securityService: SecurityService,
    private val decisionService: DecisionService,
    private val publisher: ApplicationEventPublisher
) {


    fun getTaskTeams(taskId: TaskId, pageable: Pageable): List<TaskTeam> {
        return taskTeamRepository.getTeams(taskId, pageable)
    }
    @Transactional
    fun cancelTaskTeam(teamId: TeamId, taskId: TaskId) {
        val taskTeam = taskTeamRepository.getTaskTeam(taskId, teamId)
        val taskUser = taskUserRepository.getTaskUser(taskId, securityService.userId)
        if (taskUser.role == UserTaskRole.OWNER) {
            decisionService.decide(DecisionCommand(taskTeam.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TASK_UPDATED))
        }
    }
}
