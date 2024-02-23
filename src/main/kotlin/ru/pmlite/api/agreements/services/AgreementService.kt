package ru.pmlite.api.agreements.services

import org.springframework.stereotype.Service
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.repositories.AgreementRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.values.AgreementId

@Service
class AgreementService(
    private val agreementRepository: AgreementRepository,
    private val securityService: SecurityService
) {
    fun createAgreement(type: AgreementType): AgreementId {
        return agreementRepository.createAgreement(type, securityService.userId)
    }

    fun updateAgreement(agreementId: AgreementId, state: AgreementState) {
        agreementRepository.updateAgreement(agreementId, state)
    }

    fun resetAgreement(agreementId: AgreementId) {
        agreementRepository.updateAgreement(agreementId, AgreementState.PENDING)
    }

}
