package ru.pmlite.api.account.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.dto.ProfileDetails
import ru.pmlite.api.account.dto.SavePasswordCommand
import ru.pmlite.api.account.dto.SaveProfileCommand
import ru.pmlite.api.account.repositories.AccountRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.tags.repositories.TagsRepository
import ru.pmlite.api.values.TagId

@Service
class AccountService(
    private val repository: AccountRepository,
    private val tagsRepository: TagsRepository,
    private val securityService: SecurityService
) {
    fun getAccountDetails(): AccountDetails {
        val userId = securityService.userId
        return repository.getAccountDetails(userId)
            .copy(
                roles = repository.getAccountRoles(userId),
                taskRoles = repository.getAccountTaskRoles(userId),
                teamRoles = repository.getAccountTeamRoles(userId),
                taskTeams = repository.getAccountTaskTeams(userId),
            )
    }

    fun profileDetails(): ProfileDetails {
        val userId = securityService.userId
        return repository.getProfileDetails(userId)
            .copy(
                tags = tagsRepository.getTagsByUser(userId)
            )
    }

    @Transactional
    fun saveProfile(command: SaveProfileCommand) {
        repository.saveProfile(securityService.userId, command)
        tagsRepository.addUserTags(securityService.userId, command.tags)
    }

    fun deleteTag(tagId: TagId) {
        tagsRepository.deleteUserTag(securityService.userId, tagId)
    }

    fun validatePassword(password: String) {
        securityService.validatePassword(password)
    }

    fun savePassword(command: SavePasswordCommand) {
        validatePassword(command.oldPassword)
        securityService.savePassword(command.password)
    }


}
