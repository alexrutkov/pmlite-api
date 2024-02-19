package ru.pmlite.api.agreements.domains

import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import ru.pmlite.api.values.DecisionId
import java.time.Instant

data class DecisionDetails(
    val id: DecisionId,
    val user: UserSummary,
    val comment: String,
    val decision: Decision,
    val mode: DecisionMode,
    val createdAt: Instant,
    val agreementId: AgreementId
)
