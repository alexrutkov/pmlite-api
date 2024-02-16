package ru.pmlite.api.aggreements.dto

import com.fasterxml.jackson.annotation.JsonRawValue
import ru.pmlite.api.aggreements.domains.AgreementType
import ru.pmlite.api.users.domain.UserSummary
import ru.pmlite.api.values.AgreementId
import java.time.Instant

data class AgreementSummary(
    val id: AgreementId,
    val user: UserSummary,
    val type: AgreementType,
    val createdAt: Instant,
    @JsonRawValue
    val details: String
)
