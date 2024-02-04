package ru.pmlite.api.tasks.domain

import ru.pmlite.api.tags.domains.TagDetails
import ru.pmlite.api.tasks.dto.TaskSummary
import ru.pmlite.api.values.TaskId
import java.time.Instant

data class TaskDetails(
    val id: TaskId,
    val name: String,
    val shortDescription: String,
    val createdAt: Instant,
    val users: List<TaskUser>,
    val tags: List<TagDetails>
) {
    constructor(task: TaskSummary, users: List<TaskUser>, tags: List<TagDetails>) :
            this(
                task.id, task.name, task.shortDescription, task.createdAt,
                users,
                tags
            )
}
