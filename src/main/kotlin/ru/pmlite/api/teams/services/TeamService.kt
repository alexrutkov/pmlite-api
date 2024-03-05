package ru.pmlite.api.teams.services

import org.springframework.context.ApplicationEventPublisher
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
import ru.pmlite.api.teams.domain.UserTeamRole
import ru.pmlite.api.teams.dto.CreateTeamCommand
import ru.pmlite.api.teams.dto.TeamTaskRoleDto
import ru.pmlite.api.teams.dto.TeamUserRoleDto
import ru.pmlite.api.teams.dto.UpdateTeamCommand
import ru.pmlite.api.teams.repositories.TeamRepository
import ru.pmlite.api.teams.repositories.TeamUserRepository
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.TaskId
import ru.pmlite.api.values.TeamId

@Service
class TeamService(
  private val securityService: SecurityService,
  private val tagsRepository: TagsRepository,
  private val agreementService: AgreementService,
  private val decisionService: DecisionService,
  private val repository: TeamRepository,
  private val teamUserRepository: TeamUserRepository,
  private val publisher: ApplicationEventPublisher
) {
  @Transactional
  fun createTeam(command: CreateTeamCommand) {
    val agreementId = agreementService.createAgreement(AgreementType.TEAM)

    repository.createTeam(command, agreementId)
      .also(::addOwnerUser)
      .also { tagsRepository.addTeamTags(it, command.tags) }
  }

  private fun addOwnerUser(teamId: TeamId) {
    val agreementId = addUser(teamId, UserTeamRole.OWNER)
    DecisionCommand(agreementId, Decision.APPROVE, DecisionMode.AUTO)
      .also(decisionService::decide)
  }

  private fun addUser(taskId: TeamId, role: UserTeamRole): AgreementId {
    val agreementId = agreementService.createAgreement(AgreementType.TEAM_USER)
    repository.addUserRole(
      TeamUserRoleDto(taskId, securityService.userId, role, agreementId)
    )
    return agreementId
  }
  @Transactional
  fun updateTeam(teamId: TeamId, command: UpdateTeamCommand) {
    this.repository.updateTeam(teamId, command)
    tagsRepository.addTeamTags(teamId, command.tags)
  }
  @Transactional
  fun joinToTeam(id: TeamId) {
    repository.findAgreementByTeamUser(id, securityService.userId)
      ?.also(agreementService::resetAgreement)
      ?: addUser(id, UserTeamRole.EMPLOYEE)
    publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.ROLES_UPDATED))
  }

  fun joinTeamToTask(teamId: TeamId, taskId: TaskId) {
    val teamRole = teamUserRepository.getTeamUser(teamId, securityService.userId)
    if (teamRole.role == UserTeamRole.OWNER) {
      repository.findAgreementByTeamTask(teamId, taskId)
        ?.also(agreementService::resetAgreement)
        ?: addTeamTask(teamId, taskId)
      publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.ROLES_UPDATED))
    }
  }

  private fun addTeamTask(teamId: TeamId, taskId: TaskId): AgreementId {
    val agreementId = agreementService.createAgreement(AgreementType.TASK_TEAM)
    repository.addTeamTask(
      TeamTaskRoleDto(taskId, teamId, agreementId)
    )
    return agreementId
  }
}
