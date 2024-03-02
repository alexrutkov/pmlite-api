package ru.pmlite.api.likes.controllers

import org.springframework.web.bind.annotation.*
import ru.pmlite.api.likes.domain.EntityType
import ru.pmlite.api.likes.dto.LikeDto
import ru.pmlite.api.likes.services.LikesService
import ru.pmlite.api.values.EntityId

@RestController
@RequestMapping("/api/likes")
class LikesController(
  private val service: LikesService
) {


  @PostMapping
  fun addLike(@RequestBody like: LikeDto) = service.addLike(like)

  @DeleteMapping("{entityId}")
  fun disLike(
    @RequestParam type: EntityType,
    @PathVariable entityId: Long
  ) = service.disLike(LikeDto(EntityId(entityId), type))

}
