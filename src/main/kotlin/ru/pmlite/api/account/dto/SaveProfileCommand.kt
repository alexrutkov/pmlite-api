package ru.pmlite.api.account.dto

import ru.pmlite.api.tags.dto.TagDto

data class SaveProfileCommand(
    val name: String,
    val description: String,
    val tags: List<TagDto>
)
