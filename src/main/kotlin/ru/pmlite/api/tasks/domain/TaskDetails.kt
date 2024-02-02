package ru.pmlite.api.tasks.domain

import ru.pmlite.api.tasks.dto.TaskSummary
import ru.pmlite.api.values.TaskId
import java.time.Instant

data class TaskDetails(
    val id: TaskId,
    val name: String,
    val shortDescription: String,
    val createdAt: Instant,
    val users: List<TaskUser>
) {
    constructor(task: TaskSummary, users: List<TaskUser>): this(task.id, task.name, task.shortDescription, task.createdAt, users)
}
