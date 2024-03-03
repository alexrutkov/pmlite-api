package ru.pmlite.api.tasks.dto

import ru.pmlite.api.likes.domain.LikeEntity
import ru.pmlite.api.values.TaskId
import java.time.Instant

data class TaskSummary(
    val id: TaskId,
    val name: String,
    val shortDescription: String,
    val createdAt: Instant,
    override val likeAmount: Long,
    override val starAmount: Long,
    override val isLiked: Boolean = false,
    override val isStared: Boolean = false
) : LikeEntity
