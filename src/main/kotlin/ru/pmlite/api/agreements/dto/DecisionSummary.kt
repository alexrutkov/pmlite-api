package ru.pmlite.api.agreements.dto

import ru.pmlite.api.agreements.domains.Decision
import ru.pmlite.api.agreements.domains.DecisionDetails
import ru.pmlite.api.agreements.domains.DecisionMode
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.DecisionId
import java.time.Instant

data class DecisionSummary(
    val id: DecisionId,
    val user: UserSummary,
    val comment: String,
    val decision: Decision,
    val mode: DecisionMode,
    val createdAt: Instant,
    val agreement: AgreementSummary
) {
    constructor(details: DecisionDetails, agreement: AgreementSummary)
            : this(
        details.id,
        details.user,
        details.comment,
        details.decision,
        details.mode,
        details.createdAt,
        agreement
    )
}
