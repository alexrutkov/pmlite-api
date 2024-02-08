package ru.pmlite.api.tasks.services

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import ru.pmlite.api.account.domains.AccountEventType
import ru.pmlite.api.account.events.AccountEvent
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.tasks.repositories.TaskRepository
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.UserId

@Service
class UserTaskService(
    private val taskRepository: TaskRepository,
    private val publisher: ApplicationEventPublisher
) : ApplicationRunner {



    override fun run(args: ApplicationArguments?) {
        /*taskRepository.changeUserRole(TaskId(10), UserId(5), UserTaskRole.EMPLOYEE)
        publisher.publishEvent(AccountEvent(UserId(5), AccountEventType.ROLES_UPDATED))*/
    }
}
