package ru.pmlite.api.aggreements.controllers

import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.aggreements.services.AgreementService

@RestController
@RequestMapping("/api/agreements")
class AgreementController(
    private val agreementService: AgreementService
) {

    @GetMapping("details")
    fun getPendingAgreementDetails() = agreementService.getPendingAgreementDetails()

    @GetMapping("tasks")
    fun getTasksAgreements(pageable: Pageable) = agreementService.getTasksAgreements(pageable)

    @GetMapping("taskUsers")
    fun getTaskUsersAgreements(pageable: Pageable) = agreementService.getTaskUsersAgreements(pageable)

    @PreAuthorize("hasRole('AGREEMENT_TAG')")
    @GetMapping("tags")
    fun getTagsAgreements(pageable: Pageable) = agreementService.getTagsAgreements(pageable)
}
