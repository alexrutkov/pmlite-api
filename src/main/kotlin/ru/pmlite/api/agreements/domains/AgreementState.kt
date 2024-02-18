package ru.pmlite.api.agreements.domains

enum class AgreementState {
    APPROVED, DECLINED, PENDING, CANCELLED;

    companion object {
        fun from(decision: Decision) = when(decision){
            Decision.APPROVE -> APPROVED
            Decision.DECLINE -> DECLINED
        }
    }
}
