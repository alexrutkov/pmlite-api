package ru.pmlite.api.aggreements.dto

import ru.pmlite.api.aggreements.domains.AgreementType

data class PendingAgreementDetails(
    val agreementDetails: List<PendingAgreementCount>
) {
    val totalCount = agreementDetails.sumOf(PendingAgreementCount::count)
}


data class PendingAgreementCount(
    val type: AgreementType,
    val count: Int
)
