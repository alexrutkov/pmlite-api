package ru.pmlite.api.users.domain

import ru.pmlite.api.likes.domain.LikeEntity
import ru.pmlite.api.values.EntityId
import java.time.Instant

data class UserSummary(
    val id: Long,
    val name: String
)
data class UserShortDetails(
    val id: Long,
    val name: String,
    val description: String = "",
    val createdAt: Instant = Instant.now(),
    override val likeAmount: Long,
    override val isLiked: Boolean = false
) : LikeEntity {
    val entityId get() = EntityId(id)

}


