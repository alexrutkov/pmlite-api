package ru.pmlite.api.agreements.dto

import com.fasterxml.jackson.annotation.JsonRawValue
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import java.time.Instant

data class AgreementSummary(
    val id: AgreementId,
    val user: UserSummary,
    val type: AgreementType,
    val createdAt: Instant,
    val state: AgreementState,
    @JsonRawValue
    val details: String
)
