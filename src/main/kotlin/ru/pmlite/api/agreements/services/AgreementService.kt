package ru.pmlite.api.agreements.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.dto.AgreementSummary
import ru.pmlite.api.agreements.dto.PendingAgreementCount
import ru.pmlite.api.agreements.dto.PendingAgreementDetails
import ru.pmlite.api.agreements.repositories.AgreementRepository
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
        if (isTagAllowed()) {
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

    private fun isTagAllowed() = securityService.roles.contains(UserRole.ROLE_AGREEMENT_TAG)

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

    fun getAgreementDetails(ids: List<AgreementId>): List<AgreementSummary> {
        return agreementRepository.getAgreementDetailsByIds(ids)
    }

    fun getAgreementsByType(type: AgreementType): List<AgreementId> {
        return when (type) {
            AgreementType.TASK -> agreementRepository.findPendingTask(securityService.userId)
            AgreementType.TAG -> if (isTagAllowed()) agreementRepository.findPendingTags() else emptyList()
            AgreementType.TASK_USER -> agreementRepository.findPendingUserTask(securityService.userId)
            AgreementType.TASK_TEAM -> TODO()
            AgreementType.TEAM_USER -> TODO()
        }
    }
}
