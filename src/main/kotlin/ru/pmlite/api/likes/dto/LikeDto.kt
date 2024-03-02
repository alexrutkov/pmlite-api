package ru.pmlite.api.likes.dto

import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.values.EntityId

class LikeDto(
  val entityId: EntityId,
  val type: EntityType
)
