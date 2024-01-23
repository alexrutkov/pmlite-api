package ru.pmlite.api.tasks.domain

import java.time.Instant

data class User(
    val id: Long,
    val name: String,
)

enum class UserTeamRole {
    EMPLOYEE, CHIEF
}

data class Team(
    val id: Long,
    val name: String,
    val createdAt: Instant
) {
    val tags: Set<Tag> = emptySet()
    val users: Set<TaskUser> = emptySet()
    val tasks: Set<Task> = emptySet()
}

data class TeamUser(
    val user: User,
    val role: UserTeamRole
)


data class Task(
    val id: Long,
    val name: String,
    val shortDescription: String,
    val createdAt: Instant
) {
    val tags: Set<Tag> = emptySet()
    val teams: Set<Team> = emptySet()
    val users: Set<TaskUser> = emptySet()
}

data class TaskUser(
    val user: User,
    val role: UserTaskRole
)
