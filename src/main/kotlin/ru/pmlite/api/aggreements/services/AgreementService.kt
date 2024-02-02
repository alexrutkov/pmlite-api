package ru.pmlite.api.aggreements.services

import org.springframework.stereotype.Service
import ru.pmlite.api.aggreements.domains.AgreementState
import ru.pmlite.api.aggreements.domains.AgreementType
import ru.pmlite.api.aggreements.repositories.AgreementRepository
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
}
