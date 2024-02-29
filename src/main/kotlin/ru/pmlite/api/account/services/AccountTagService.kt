package ru.pmlite.api.account.services

import org.springframework.stereotype.Service
import ru.pmlite.api.account.domains.AccountTag
import ru.pmlite.api.account.domains.ActivityState
import ru.pmlite.api.account.dto.UpdateActiveCommand
import ru.pmlite.api.account.repositories.AccountTagRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.tags.dto.TagDto
import ru.pmlite.api.values.AccountTagId

@Service
class AccountTagService(
  private val securityService: SecurityService,
  private val accountTagRepository: AccountTagRepository
) {
  fun getAccountTags(): List<AccountTag> {
    return accountTagRepository.getAccountTagsByUser(securityService.userId)
  }

  fun addAccountTags(tags: List<TagDto>) {
    accountTagRepository.addAccountTags(
      securityService.userId,
      tags
    )
  }

  fun deleteAccountTag(accountTagId: AccountTagId) {
    accountTagRepository.updateState(accountTagId, securityService.userId, ActivityState.CANCELLED)
  }

  fun updateActivity(accountTagId: AccountTagId, command: UpdateActiveCommand) {
    accountTagRepository.updateState(
      accountTagId, securityService.userId,
      if (command.isActive) ActivityState.ACTIVE else ActivityState.IN_ACTIVE
    )
  }
}
