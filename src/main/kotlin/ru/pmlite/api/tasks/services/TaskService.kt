package ru.pmlite.api.tasks.services

import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.account.domains.AccountEventType
import ru.pmlite.api.account.events.AccountEvent
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.domains.Decision
import ru.pmlite.api.agreements.domains.DecisionMode
import ru.pmlite.api.agreements.dto.DecisionCommand
import ru.pmlite.api.agreements.services.AgreementService
import ru.pmlite.api.agreements.services.DecisionService
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.tags.repositories.TagsRepository
import ru.pmlite.api.tasks.domain.TaskDetails
import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.tasks.dto.CreateTaskCommand
import ru.pmlite.api.tasks.dto.TaskSummary
import ru.pmlite.api.tasks.dto.TaskUserRoleDto
import ru.pmlite.api.tasks.dto.UpdateTaskCommand
import ru.pmlite.api.tasks.repositories.SearchTaskRepository
import ru.pmlite.api.tasks.repositories.TaskRepository
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TagId
import ru.pmlite.api.values.TaskId

private val logger = KotlinLogging.logger {}
@Service
class TaskService(
    private val securityService: SecurityService,
    private val tagsRepository: TagsRepository,
    private val agreementService: AgreementService,
    private val decisionService: DecisionService,
    private val repository: TaskRepository,
    private val searchRepository: SearchTaskRepository,
    private val publisher: ApplicationEventPublisher
) {
    @Transactional
    fun createTask(command: CreateTaskCommand) {
        val agreementId = agreementService.createAgreement(AgreementType.TASK)
        addTask(command, agreementId)
    }
    @Transactional
    fun createRootTask(command: CreateTaskCommand) {
        val agreementId = agreementService.createAgreement(AgreementType.TASK)
        DecisionCommand(agreementId, Decision.APPROVE, DecisionMode.AUTO)
            .also(decisionService::decide)
        addTask(command, agreementId)
    }

    private fun addTask(
        command: CreateTaskCommand,
        agreementId: AgreementId
    ) {
        repository.createTask(command, agreementId)
            .also(::addOwnerUser)
            .also { tagsRepository.addTaskTags(it, command.tags) }
    }

    @Transactional
    fun joinToTask(id: TaskId) {
        repository.findAgreementByUserTask(id, securityService.userId)
            ?.also(agreementService::resetAgreement)
            ?: addUser(id, UserTaskRole.EMPLOYEE)
        publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.ROLES_UPDATED))
    }

    private fun addOwnerUser(taskId: TaskId) {
        val agreementId = addUser(taskId, UserTaskRole.OWNER)
        DecisionCommand(agreementId, Decision.APPROVE, DecisionMode.AUTO)
            .also(decisionService::decide)

    }

    private fun addUser(taskId: TaskId, role: UserTaskRole): AgreementId {
        val agreementId = agreementService.createAgreement(AgreementType.TASK_USER)
        repository.addUserRole(
            TaskUserRoleDto(taskId, securityService.userId, role, agreementId)
        )
        return agreementId
    }

    fun getAllTasks(pageable: Pageable): List<TaskSummary> {
        return repository.getAllTasks(securityService.userId, pageable)
    }

    fun getMyTasks(pageable: Pageable): List<TaskSummary>  {
        return repository.getMyTasks(securityService.userId, pageable)
    }

    fun getTask(id: TaskId): TaskDetails {
        return repository.getTask(id)
    }

    @Transactional
    fun updateTask(taskId: TaskId, command: UpdateTaskCommand) {
        this.repository.updateTask(taskId, command)
        tagsRepository.addTaskTags(taskId, command.tags)
    }

    fun deleteTaskTag(taskId: TaskId, tagId: TagId) {
        tagsRepository.deleteTaskTag(taskId, tagId)
    }

    fun searchAllTasks(search: String, pageable: Pageable): List<TaskSummary> {
        return searchRepository.searchAllTasks(search, pageable)
    }

    fun searchMyTasks(search: String, pageable: Pageable): List<TaskSummary> {
        return searchRepository.searchMyTasks(search, securityService.userId, pageable)
    }


}
