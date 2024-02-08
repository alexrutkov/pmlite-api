package ru.pmlite.api.account.events

import ru.pmlite.api.account.domains.AccountEventType
import ru.pmlite.api.values.UserId

data class AccountEvent(
    val userId: UserId,
    val type: AccountEventType
)
