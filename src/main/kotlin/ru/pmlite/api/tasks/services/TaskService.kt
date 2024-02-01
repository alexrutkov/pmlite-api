package ru.pmlite.api.tasks.services

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.dto.TaskUserRoleDto
import ru.pmlite.api.tasks.repositories.TaskRepository
import ru.pmlite.api.values.TaskId

private val logger = KotlinLogging.logger {}
@Service
class TaskService(
    private val securityService: SecurityService,
    private val repository: TaskRepository
) {
    @Transactional
    fun createTask(command: CreateTaskCommand) {
        repository.createTask(command)
            .also(::addOwnerUser)
    }

    private fun addOwnerUser(taskId: TaskId) {
        repository.addUserRole(
            TaskUserRoleDto(taskId, securityService.userId, UserTaskRole.OWNER)
        )
    }
}
