package ru.pmlite.api.account.domains

import ru.pmlite.api.tags.domains.TagDetails
import ru.pmlite.api.values.AccountTagId
import java.time.Instant

data class AccountTag(
    val id: AccountTagId,
    val tag: TagDetails,
    val state: ActivityState,
    val createdAt: Instant
)
