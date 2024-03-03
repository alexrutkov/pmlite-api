package ru.pmlite.api.teams.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import ru.pmlite.api.tags.dto.TagDto

data class UpdateTeamCommand(
  @field: NotBlank
  @field: Size(min = 2, max = 50)
  val name: String,
  @field: NotBlank
  @field: Size(min = 2, max = 255)
  val shortDescription: String,
  val tags: List<TagDto>
)
