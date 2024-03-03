package ru.pmlite.api.tasks.domain

import ru.pmlite.api.users.domain.UserShortDetails
import java.time.Instant

enum class UserTeamRole {
    EMPLOYEE, OWNER
}

data class Team(
    val id: Long,
    val name: String,
    val createdAt: Instant
) {
//    val tags: Set<Tag> = emptySet()
    val users: Set<TaskUser> = emptySet()
//    val tasks: Set<Task> = emptySet()
}

data class TeamUser(
    val user: UserShortDetails,
    val role: UserTeamRole
)

