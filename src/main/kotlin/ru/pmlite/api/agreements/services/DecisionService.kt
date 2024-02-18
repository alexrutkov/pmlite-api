package ru.pmlite.api.agreements.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import ru.pmlite.api.account.domains.AccountEventType
import ru.pmlite.api.account.events.AccountEvent
import ru.pmlite.api.agreements.domains.AgreementState
import ru.pmlite.api.agreements.dto.DecisionCommand
import ru.pmlite.api.agreements.exceptions.DecisionNotAllowedException
import ru.pmlite.api.agreements.repositories.DecisionRepository
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.values.AgreementId

@Service
class DecisionService(
    private val decisionRepository: DecisionRepository,
    private val securityService: SecurityService,
    private val agreementService: AgreementService,
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
        return agreementService.getAgreementDetails(agreementId)
            ?.let { agreementService.getAgreementsByType(it.type)
                .contains(agreementId)
            } ?: false
    }
}
