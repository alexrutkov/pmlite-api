package ru.pmlite.api.security.controllers

import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.security.providers.JwtTokenProvider
import ru.pmlite.api.security.services.SecurityService

private val logger = KotlinLogging.logger {}
@RequestMapping("/api/authorization")
@RestController
class AuthenticatedUserController(
    private val securityService: SecurityService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("createToken")
    fun createToken(
        @RequestPart username: String,
        @RequestPart password: String,
        response: HttpServletResponse
    ) {
        runCatching {
            securityService.authenticate(username, password)
            .let(jwtTokenProvider::createTokenByAuthentication)
            .let(jwtTokenProvider::getAuthenticationCookieByToken)
            .also(response::addCookie)
        }.onFailure {
         when(it) {
          is BadCredentialsException -> response.status = HttpStatus.UNPROCESSABLE_ENTITY.value()
          is DisabledException -> response.status = HttpStatus.CONFLICT.value()
         }
        }
    }

    @GetMapping("isAuthorized")
    fun isAuthorized() = securityService.isAuthorized
}
