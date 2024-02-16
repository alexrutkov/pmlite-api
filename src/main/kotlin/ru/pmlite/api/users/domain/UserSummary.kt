package ru.pmlite.api.users.domain

import java.time.Instant

data class UserSummary(
    val id: Long,
    val name: String
)
data class UserShortDetails(
    val id: Long,
    val name: String,
    val description: String = "",
    val createdAt: Instant = Instant.now()
)


