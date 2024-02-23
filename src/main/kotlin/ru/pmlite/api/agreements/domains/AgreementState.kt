package ru.pmlite.api.agreements.domains

enum class AgreementState {
    APPROVED, PENDING, CANCELLED;

    companion object {
        fun from(decision: Decision) = when(decision){
            Decision.APPROVE -> APPROVED
            Decision.DECLINE -> CANCELLED
        }
    }
}
