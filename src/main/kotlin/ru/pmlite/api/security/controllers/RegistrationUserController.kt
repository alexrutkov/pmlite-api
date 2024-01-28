package ru.pmlite.api.security.controllers

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.pmlite.api.security.dto.RegistrationCommand
import ru.pmlite.api.security.dto.ValidateEmailCommand
import ru.pmlite.api.security.providers.JwtTokenProvider
import ru.pmlite.api.security.services.RegistrationService
import ru.pmlite.api.security.validators.DefaultRecaptchaValidator
import ru.pmlite.api.security.validators.DtoValidator
import ru.pmlite.api.security.validators.EmailValidator

private val logger = KotlinLogging.logger {}
@RestController
@RequestMapping("/api/registration")
class RegistrationUserController(
    private val emailValidator: EmailValidator,
    private val recaptchaValidator: DefaultRecaptchaValidator,
    private val dtoValidator: DtoValidator,
    private val registrationService: RegistrationService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    @PostMapping("validateEmail")
    fun validateEmail(@RequestBody command: ValidateEmailCommand) = emailValidator.notExistsValidate(command.email)


    @PostMapping
    fun registration(
        @Valid @RequestBody command: RegistrationCommand,
        result: BindingResult,
        response: HttpServletResponse
    ) {
        dtoValidator.validate(result)
        recaptchaValidator.validate(command.recaptcha)
        emailValidator.notExistsValidate(command.email)

        registrationService.register(command)
            .let(jwtTokenProvider::createTokenByAuthentication)
            .let(jwtTokenProvider::getAuthenticationCookieByToken)
            .also(response::addCookie)
    }
}
