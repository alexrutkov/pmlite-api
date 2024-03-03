package ru.pmlite.api.teams.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import ru.pmlite.api.tags.dto.TagDto

data class CreateTeamCommand(
  @field: NotBlank
  @field: Size(min = 2, max = 255)
  val name: String,
  @field: NotBlank
  @field: Size(min = 2, max = 255)
  val description: String,
  val tags: List<TagDto> = emptyList()
)

