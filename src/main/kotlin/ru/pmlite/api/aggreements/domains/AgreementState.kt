package ru.pmlite.api.aggreements.domains

enum class AgreementState {
    APPROVED, DECLINED, PENDING;

    companion object {
        fun from(decision: Decision) = when(decision){
            Decision.APPROVE -> APPROVED
            Decision.DECLINE -> DECLINED
        }
    }
}
