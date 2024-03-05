package ru.pmlite.api.agreements.controllers

import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.agreements.dto.DecisionCommand
import ru.pmlite.api.agreements.dto.UserDecisionCommand
import ru.pmlite.api.agreements.services.AgreementDetailsService
import ru.pmlite.api.agreements.services.DecisionService
import ru.pmlite.api.values.AgreementId

@RestController
@RequestMapping("/api/agreements")
class AgreementController(
    private val agreementService: AgreementDetailsService,
    private val decisionService: DecisionService
) {

    @GetMapping("details")
    fun getPendingAgreementDetails() = agreementService.getPendingAgreementDetails()

    @GetMapping("tasks")
    fun getTasksAgreements(pageable: Pageable) = agreementService.getTasksAgreements(pageable)

    @GetMapping("taskUsers")
    fun getTaskUsersAgreements(pageable: Pageable) = agreementService.getTaskUsersAgreements(pageable)

    @GetMapping("teamUsers")
    fun getTeamUsersAgreements(pageable: Pageable) = agreementService.getTeamUsersAgreements(pageable)

    @GetMapping("taskTeams")
    fun getTaskTeamsAgreements(pageable: Pageable) = agreementService.getTaskTeamsAgreements(pageable)

    @PreAuthorize("hasRole('AGREEMENT_TAG')")
    @GetMapping("tags")
    fun getTagsAgreements(pageable: Pageable) = agreementService.getTagsAgreements(pageable)

    @PreAuthorize("hasRole('AGREEMENT_TEAM')")
    @GetMapping("teams")
    fun getTeamsAgreements(pageable: Pageable) = agreementService.getTeamsAgreements(pageable)

    @GetMapping("{id}")
    fun getAgreementDetails(@PathVariable id: Long) = agreementService.getAgreementDetails(listOf(AgreementId(id)))
        .firstOrNull()

    @PostMapping("{id}/decision")
    fun decision(
        @PathVariable id: Long,
        @RequestBody command: UserDecisionCommand
    ) = decisionService.decide(DecisionCommand(AgreementId(id), command.decision, comment = command.comment))

    @GetMapping("{id}/decision")
    fun canDoDecision(@PathVariable id: Long) = decisionService.canDoDecision(AgreementId(id))
}
