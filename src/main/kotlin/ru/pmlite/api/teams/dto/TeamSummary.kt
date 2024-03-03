package ru.pmlite.api.teams.dto

import ru.pmlite.api.likes.domain.LikeEntity
import ru.pmlite.api.values.TeamId
import java.time.Instant

data class TeamSummary(
  val id: TeamId,
  val name: String,
  val description: String,
  val createdAt: Instant,
  override val likeAmount: Long,
  override val starAmount: Long,
  override val isLiked: Boolean = false,
  override val isStared: Boolean = false
) : LikeEntity
