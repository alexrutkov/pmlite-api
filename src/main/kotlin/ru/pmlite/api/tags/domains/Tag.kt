package ru.pmlite.api.tags.domains

import ru.pmlite.api.values.TagId


data class Tag(
    val tagId: TagId,
    val tag: String
) {
    val displayTag get() = tag.lowercase().replaceFirstChar(Char::titlecase)
}
