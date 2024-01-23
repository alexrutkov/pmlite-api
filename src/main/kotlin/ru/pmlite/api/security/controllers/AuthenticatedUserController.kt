package ru.pmlite.api.security.controllers

import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.security.dto.CreateTokenCommand
import ru.pmlite.api.security.services.SecurityService

private val logger = KotlinLogging.logger {}
@RequestMapping("/api/authorization")
@RestController
class AuthenticatedUserController(
    private val securityService: SecurityService
) {

    @PostMapping("createToken")
    fun createToken(
        @RequestBody command: CreateTokenCommand
    ) {
        logger.info(command.toString())
    }

    @GetMapping("isAuthorized")
    fun isAuthorized() = securityService.isAuthorized
}
