package ru.pmlite.api.agreements.controllers

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.agreements.domains.AgreementType
import ru.pmlite.api.agreements.services.DecisionService

@RestController
@RequestMapping("/api/decisions")
class DecisionControllers(
    private val decisionService: DecisionService
) {

    @GetMapping
    fun getDecisions(
        @RequestParam type: AgreementType?,
        pageable: Pageable
    ) = decisionService.getDecisions(type, pageable)

    @GetMapping("my")
    fun getMyDecisions(
        @RequestParam type: AgreementType?,
        pageable: Pageable
    )  = decisionService.getMyDecisions(type, pageable)
}
