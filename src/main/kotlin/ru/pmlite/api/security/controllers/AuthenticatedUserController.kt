package ru.pmlite.api.security.controllers

import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.security.dto.CreateTokenCommand

private val logger = KotlinLogging.logger {}
@RequestMapping("/api")
@RestController
class AuthenticatedUserController {

    @PostMapping("createToken")
    fun createToken(
        @RequestBody command: CreateTokenCommand
    ) {
        logger.info(command.toString())
    }
}
