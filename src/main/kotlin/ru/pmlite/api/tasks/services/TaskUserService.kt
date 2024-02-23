package ru.pmlite.api.tasks.services

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
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
import ru.pmlite.api.tasks.domain.TaskUser
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.tasks.repositories.TaskRepository
import ru.pmlite.api.tasks.repositories.TaskUserRepository
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Service
class TaskUserService(
    private val taskRepository: TaskRepository,
    private val taskUserRepository: TaskUserRepository,
    private val securityService: SecurityService,
    private val decisionService: DecisionService,
    private val publisher: ApplicationEventPublisher
) : ApplicationRunner {



    override fun run(args: ApplicationArguments?) {
        /*taskRepository.changeUserRole(TaskId(10), UserId(5), UserTaskRole.EMPLOYEE)
        publisher.publishEvent(AccountEvent(UserId(5), AccountEventType.ROLES_UPDATED))*/
    }

    fun getUsers(taskId: TaskId, pageable: Pageable): List<TaskUser> {
        return taskUserRepository.getUserRelation(taskId, pageable)
    }

    private fun isAllowedTaskOperation(taskUser: TaskId): Boolean {
        return taskUserRepository.getTaskUser(taskUser, securityService.userId).role == UserTaskRole.OWNER
    }

    @Transactional
    fun cancelUser(taskId: TaskId, userId: UserId) {
        val userTask = taskUserRepository.getTaskUser(taskId, userId)
        if (isAllowedTaskOperation(taskId)) {
            decisionService.decide(DecisionCommand(userTask.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TASK_UPDATED))
        }
    }

    fun cancelTask(id: TaskId) {
        val userTask = taskUserRepository.getTaskUser(id, securityService.userId)
        if (userTask.role != UserTaskRole.OWNER) {
            decisionService.decide(DecisionCommand(userTask.agreementId, Decision.DECLINE))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.TASK_UPDATED))
        }
    }
}
