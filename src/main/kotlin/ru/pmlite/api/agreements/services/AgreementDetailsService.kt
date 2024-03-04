package ru.pmlite.api.agreements.services

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.dto.AgreementSummary
import ru.pmlite.api.agreements.dto.PendingAgreementCount
import ru.pmlite.api.agreements.dto.PendingAgreementDetails
import ru.pmlite.api.agreements.repositories.AgreementRepository
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.values.AgreementId

@Service
class AgreementDetailsService(
    private val agreementRepository: AgreementRepository,
    private val securityService: SecurityService
) {

    fun getPendingAgreementDetails(): PendingAgreementDetails {
        val pending = mutableListOf<PendingAgreementCount>()
        if (isTagAllowed()) {
            pending.add(PendingAgreementCount(AgreementType.TAG, agreementRepository.findTags().size))
        }
        if (isTeamAllowed()) {
            pending.add(PendingAgreementCount(AgreementType.TEAM, agreementRepository.findTeamAgreements().size))
        }
        pending.add(
            PendingAgreementCount(
                AgreementType.TASK,
                agreementRepository.findTask(securityService.userId).size
            )
        )
        pending.add(
            PendingAgreementCount(
                AgreementType.TASK_USER,
                agreementRepository.findUserTask(securityService.userId).size
            )
        )
        pending.add(
            PendingAgreementCount(
                AgreementType.TEAM_USER,
                agreementRepository.findTeamUsers(securityService.userId).size
            )
        )
        return PendingAgreementDetails(pending)
    }

    private fun isTagAllowed() = securityService.roles.contains(UserRole.ROLE_AGREEMENT_TAG)

    private fun isTeamAllowed() = securityService.roles.contains(UserRole.ROLE_AGREEMENT_TEAM)

    fun getTasksAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findTask(securityService.userId, pageable = pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getTaskUsersAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findUserTask(securityService.userId, pageable = pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getTeamsAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findTeamAgreements(pageable = pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getTagsAgreements(pageable: Pageable): List<AgreementSummary> {
        return agreementRepository.findTags(pageable = pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getTeamUsersAgreements(pageable: Pageable): Any {
        return agreementRepository.findTeamUsers(securityService.userId, pageable = pageable)
            .let(agreementRepository::getAgreementDetailsByIds)
    }

    fun getAgreementDetails(ids: List<AgreementId>): List<AgreementSummary> {
        return agreementRepository.getAgreementDetailsByIds(ids)
    }

    fun getAgreementsByType(agreement: AgreementSummary): List<AgreementId> {
        return when (agreement.type) {
            AgreementType.TASK -> agreementRepository.findTask(securityService.userId, agreement.id)
            AgreementType.TAG -> if (isTagAllowed()) agreementRepository.findTags(agreement.id) else emptyList()
            AgreementType.TASK_USER -> agreementRepository.findUserTask(securityService.userId, agreement.id)
            AgreementType.TEAM -> if (isTeamAllowed()) agreementRepository.findTeamAgreements(agreement.id) else emptyList()
            AgreementType.TASK_TEAM -> TODO()
            AgreementType.TEAM_USER -> agreementRepository.findTeamUsers(securityService.userId, agreement.id)
        }
    }


}
