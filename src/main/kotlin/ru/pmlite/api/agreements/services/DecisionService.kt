package ru.pmlite.api.agreements.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.pmlite.api.account.domains.AccountEventType
import ru.pmlite.api.account.events.AccountEvent
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.domains.DecisionDetails
import ru.pmlite.api.agreements.dto.AgreementSummary
import ru.pmlite.api.agreements.dto.DecisionCommand
import ru.pmlite.api.agreements.dto.DecisionSummary
import ru.pmlite.api.agreements.exceptions.DecisionNotAllowedException
import ru.pmlite.api.agreements.repositories.DecisionRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.values.AgreementId

@Service
class DecisionService(
    private val decisionRepository: DecisionRepository,
    private val securityService: SecurityService,
    private val agreementService: AgreementService,
    private val agreementDetailsService: AgreementDetailsService,
    private val publisher: ApplicationEventPublisher
) {
    fun decide(command: DecisionCommand) {
        if (isDecisionAllowed(command.agreementId)) {
            decisionRepository.saveDecision(command, securityService.userId)
            agreementService.updateAgreement(command.agreementId, AgreementState.from(command.decision))
            publisher.publishEvent(AccountEvent(securityService.userId, AccountEventType.AGREEMENTS_UPDATED))
        } else throw DecisionNotAllowedException()
    }

    private fun isDecisionAllowed(agreementId: AgreementId): Boolean {
        val agreement = agreementDetailsService.getAgreementDetails(listOf(agreementId)).first()
        return isAgreementOwner(agreement)
                || agreementDetailsService.getAgreementsByType(agreement).contains(agreementId)
    }

    private fun isAgreementOwner(agreement: AgreementSummary) =
        agreement.user.id == securityService.userId.id

    fun getDecisions(type: AgreementType?, pageable: Pageable): List<DecisionSummary> {
        return decisionRepository.getDecisions(type, pageable)
            .let(::loadDetails)
    }

    private fun loadDetails(decisions: List<DecisionDetails>): List<DecisionSummary> {
        val agreements = agreementDetailsService.getAgreementDetails(decisions.map(DecisionDetails::agreementId))
        return decisions.mapNotNull { d ->
            agreements.find { d.agreementId == it.id }
                ?.let { DecisionSummary(d, it) }
        }
    }

    fun canDoDecision(agreementId: AgreementId) {
        isDecisionAllowed(agreementId).takeIf { it } ?: throw DecisionNotAllowedException()
    }

    fun getMyDecisions(type: AgreementType?, pageable: Pageable): List<DecisionSummary> {
        return decisionRepository.getMyDecisions(securityService.userId, type, pageable)
            .let(::loadDetails)
    }
}
