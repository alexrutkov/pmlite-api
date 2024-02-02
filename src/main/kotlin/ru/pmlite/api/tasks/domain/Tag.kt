package ru.pmlite.api.tasks.domain

import ru.pmlite.api.aggreements.domains.AgreementState
import java.time.Instant

data class Tag(
    val id: Long,
    val name: String,
    val state: AgreementState,
    val createdAt: Instant
)
