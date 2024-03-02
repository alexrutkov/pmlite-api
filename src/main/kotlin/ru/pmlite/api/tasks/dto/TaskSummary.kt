package ru.pmlite.api.tasks.dto

import ru.pmlite.api.values.TaskId
import java.time.Instant

data class TaskSummary(
    val id: TaskId,
    val name: String,
    val shortDescription: String,
    val createdAt: Instant,
    val likeAmount: Long,
    val isLiked: Boolean = false
)
