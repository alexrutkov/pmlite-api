package ru.pmlite.api.account.services

import org.springframework.stereotype.Service
import ru.pmlite.api.account.dto.AccountDetails
import ru.pmlite.api.account.repositories.AccountRepository
import ru.pmlite.api.values.UserId

@Service
class AccountService(
    private val repository: AccountRepository
) {
    fun getAccountDetails(userId: UserId): AccountDetails {
        return repository.getAccountDetails(userId)
            .copy(roles = repository.getAccountRoles(userId))
    }


}
