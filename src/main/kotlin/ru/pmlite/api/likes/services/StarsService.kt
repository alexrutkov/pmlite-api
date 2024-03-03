package ru.pmlite.api.likes.services

import org.springframework.stereotype.Service
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.dto.EntityDto
import ru.pmlite.api.likes.repositories.StarsRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.values.EntityId

@Service
class StarsService(
  private val repository: StarsRepository,
  private val securityService: SecurityService
) {
  fun addStar(entity: EntityDto) {
    repository.addStar(securityService.userId, entity)
  }

  fun getMyStars(entities: List<EntityId>, type: EntityType): List<EntityId> {
    return if (entities.isNotEmpty()) repository.findStarsBy(securityService.userId, entities, type) else emptyList()
  }

  fun disStar(entity: EntityDto) {
    repository.disStar(securityService.userId, entity)
  }
}
