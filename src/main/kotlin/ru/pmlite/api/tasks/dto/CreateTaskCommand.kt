package ru.pmlite.api.tasks.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import ru.pmlite.api.tags.domains.Tag


data class UpdateTaskCommand(
    @field: NotBlank
    @field: Size(min = 2, max = 50)
    val name: String,
    @field: NotBlank
    @field: Size(min = 2, max = 255)
    val shortDescription: String,
    val tags: List<Tag>
)

data class CreateTaskCommand(
    @field: NotBlank
    @field: Size(min = 2, max = 255)
    val name: String,
    @field: NotBlank
    @field: Size(min = 2, max = 255)
    val shortDescription: String,
    @field: NotNull
    val parentId: Long? = null,
    val tags: List<Tag> = emptyList()
)
