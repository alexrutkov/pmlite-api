package ru.pmlite.api.tasks.domain

import ru.pmlite.api.users.domain.UserSummary
import java.time.Instant

data class TaskUser(
    val user: UserSummary,
    val role: UserTaskRole,
    val createdAt: Instant
)
