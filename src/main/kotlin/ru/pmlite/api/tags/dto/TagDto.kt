package ru.pmlite.api.tags.dto

import ru.pmlite.api.values.TagId

data class TagDto(
    val tagId: TagId,
    val tag: String
)
