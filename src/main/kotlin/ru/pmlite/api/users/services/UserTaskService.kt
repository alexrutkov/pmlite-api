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
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.users.domain.UserTask
import ru.pmlite.api.users.repositories.UserTaskRepository
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Service
class UserTaskService(
    private val userTaskRepository: UserTaskRepository,
    private val securityService: SecurityService,
    private val decisionService: DecisionService,
    private val publisher: ApplicationEventPublisher
) {


    fun getTasks(userId: UserId, pageable: Pageable): List<UserTask> {
        return userTaskRepository.getTaskRelation(userId, pageable)
    }


    @Transactional
    fun cancelTask(taskId: TaskId) {
        val userTask = userTaskRepository.getUserTask(securityService.userId, taskId)
        if (userTask.role != UserTaskRole.OWNER) {
            decisionService.decide(DecisionCommand(userTask.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TASK_UPDATED))
        }
    }
}
