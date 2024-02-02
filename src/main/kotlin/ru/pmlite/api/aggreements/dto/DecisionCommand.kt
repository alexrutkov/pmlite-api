package ru.pmlite.api.aggreements.dto

import ru.pmlite.api.aggreements.domains.Decision
import ru.pmlite.api.aggreements.domains.DecisionMode
import ru.pmlite.api.values.AgreementId

data class DecisionCommand(
    val agreementId: AgreementId,
    val decision: Decision,
    val mode: DecisionMode = DecisionMode.MANUAL,
    val comment: String = ""
)
