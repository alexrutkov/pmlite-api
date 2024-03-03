package ru.pmlite.api.likes.services

import org.springframework.stereotype.Service
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.dto.EntityDto
import ru.pmlite.api.likes.repositories.LikesRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.values.EntityId

@Service
class LikesService(
  private val repository: LikesRepository,
  private val securityService: SecurityService
) {
  fun addLike(like: EntityDto) {
    repository.addLike(securityService.userId, like)
  }

  fun getMyLikes(entities: List<EntityId>, type: EntityType): List<EntityId> {
    return if (entities.isNotEmpty()) repository.findLikesBy(securityService.userId, entities, type) else emptyList()
  }

  fun disLike(like: EntityDto) {
    repository.disLike(securityService.userId, like)
  }
}
