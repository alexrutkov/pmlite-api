package ru.pmlite.api.tags.domains

import ru.pmlite.api.aggreements.domains.AgreementState
import ru.pmlite.api.values.TagId

data class TagDetails(
    val tagId: TagId,
    val tag: String,
    val state: AgreementState
) {
    val displayTag get() = tag.lowercase().replaceFirstChar(Char::titlecase)

}
