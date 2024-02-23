package ru.pmlite.api.account.dto

import ru.pmlite.api.tags.domains.TagDetails

data class ProfileDetails(
    val name: String,
    val description: String,
    val tags: List<TagDetails>
)
