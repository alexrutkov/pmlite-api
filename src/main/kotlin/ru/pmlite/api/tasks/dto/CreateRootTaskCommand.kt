package ru.pmlite.api.tasks.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import ru.pmlite.api.tags.domains.Tag

data class CreateRootTaskCommand(
    @field: NotBlank
    @field: Size(min = 2, max = 255)
    val name: String,
    @field: NotBlank
    @field: Size(min = 2, max = 255)
    val shortDescription: String,
    val tags: List<Tag>
)
