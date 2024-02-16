package ru.pmlite.api.users.domain

import ru.pmlite.api.tasks.domain.UserTaskRole
import ru.pmlite.api.values.TaskId
import java.time.Instant

data class UserTask(
    val id: TaskId,
    val userId: Long,
    val name: String,
    val role: UserTaskRole,
    val createdAt: Instant
)
