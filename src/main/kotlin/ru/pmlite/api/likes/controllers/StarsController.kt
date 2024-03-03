package ru.pmlite.api.likes.controllers

import org.springframework.web.bind.annotation.*
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.dto.EntityDto
import ru.pmlite.api.likes.services.StarsService
import ru.pmlite.api.values.EntityId

@RestController
@RequestMapping("/api/stars")
class StarsController(
  private val service: StarsService
) {

  @PostMapping
  fun addLike(@RequestBody entity: EntityDto) = service.addStar(entity)

  @DeleteMapping("{entityId}")
  fun disLike(
    @RequestParam type: EntityType,
    @PathVariable entityId: Long
  ) = service.disStar(EntityDto(EntityId(entityId), type))
}
