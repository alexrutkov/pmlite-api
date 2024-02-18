package ru.pmlite.api.agreements.dto

import ru.pmlite.api.agreements.domains.AgreementType

data class PendingAgreementDetails(
    val agreementDetails: List<PendingAgreementCount>
) {
    val totalCount = agreementDetails.sumOf(PendingAgreementCount::count)
}


data class PendingAgreementCount(
    val type: AgreementType,
    val count: Int
)
