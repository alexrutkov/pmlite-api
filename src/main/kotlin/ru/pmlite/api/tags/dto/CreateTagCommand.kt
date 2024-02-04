package ru.pmlite.api.tags.dto

import jakarta.validation.constraints.Size

data class CreateTagCommand(
    @field: Size(min = 1, max = 25)
    val tag: String
)
