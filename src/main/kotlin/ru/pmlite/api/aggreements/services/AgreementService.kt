package ru.pmlite.api.aggreements.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.aggreements.domains.AgreementState
import ru.pmlite.api.aggreements.domains.AgreementType
import ru.pmlite.api.aggreements.dto.AgreementSummary
import ru.pmlite.api.aggreements.dto.PendingAgreementCount
import ru.pmlite.api.aggreements.dto.PendingAgreementDetails
import ru.pmlite.api.aggreements.repositories.AgreementRepository
import ru.pmlite.api.security.domain.UserRole
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

    fun getPendingAgreementDetails(): PendingAgreementDetails {
        val pending = mutableListOf<PendingAgreementCount>()
        if (securityService.roles.contains(UserRole.ROLE_AGREEMENT_TAG)) {
            pending.add(
                PendingAgreementCount(
                    AgreementType.TAG,
                    agreementRepository.findPendingTags().size
                )
            )
        }
        pending.add(
            PendingAgreementCount(
                AgreementType.TASK,
                agreementRepository.findPendingTask(securityService.userId).size
            )
        )
        pending.add(
            PendingAgreementCount(
                AgreementType.TASK_USER,
                agreementRepository.findPendingUserTask(securityService.userId).size
            )
        )
        return PendingAgreementDetails(pending)
    }

    fun getTasksAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findPendingTask(securityService.userId, pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getTaskUsersAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findPendingUserTask(securityService.userId, pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getTagsAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findPendingTags(pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }
}
