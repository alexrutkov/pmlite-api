package ru.pmlite.api.aggreements.services

import org.springframework.stereotype.Service
import ru.pmlite.api.aggreements.domains.AgreementState
import ru.pmlite.api.aggreements.dto.DecisionCommand
import ru.pmlite.api.aggreements.repositories.DecisionRepository
import ru.pmlite.api.security.services.SecurityService

@Service
class DecisionService(
    private val decisionRepository: DecisionRepository,
    private val securityService: SecurityService,
    private val agreementService: AgreementService
) {
    fun decide(command: DecisionCommand) {
        decisionRepository.saveDecision(command, securityService.userId)
        agreementService.updateAgreement(command.agreementId, AgreementState.from(command.decision))
    }
}
