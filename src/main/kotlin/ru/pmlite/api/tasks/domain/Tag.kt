package ru.pmlite.api.tasks.domain

import java.time.Instant

enum class AgreementState {
    APPROVED, DECLINED, PENDING
}
data class Tag(
    val id: Long,
    val name: String,
    val state: AgreementState,
    val createdAt: Instant
)
