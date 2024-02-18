package ru.pmlite.api.agreements.dto

import ru.pmlite.api.agreements.domains.Decision
import ru.pmlite.api.agreements.domains.DecisionMode
import ru.pmlite.api.values.AgreementId

data class DecisionCommand(
    val agreementId: AgreementId,
    val decision: Decision,
    val mode: DecisionMode = DecisionMode.MANUAL,
    val comment: String = ""
)
