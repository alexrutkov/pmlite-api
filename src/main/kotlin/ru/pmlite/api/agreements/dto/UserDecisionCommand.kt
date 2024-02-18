package ru.pmlite.api.agreements.dto

import ru.pmlite.api.agreements.domains.Decision

data class UserDecisionCommand(
    val decision: Decision,
    val comment: String
)
