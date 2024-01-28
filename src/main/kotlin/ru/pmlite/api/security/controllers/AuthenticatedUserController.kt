package ru.pmlite.api.security.controllers

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.pmlite.api.security.dto.PasswordRecoveryCommand
import ru.pmlite.api.security.dto.SaveRecoveryPasswordCommand
import ru.pmlite.api.security.dto.UserTokenDto
import ru.pmlite.api.security.dto.ValidateResult
import ru.pmlite.api.security.providers.JwtTokenProvider
import ru.pmlite.api.security.services.RecoveryPasswordService
import ru.pmlite.api.security.services.SecurityService
import ru.pmlite.api.security.validators.*

private val logger = KotlinLogging.logger {}
@RequestMapping("/api/authorization")
@RestController
class AuthenticatedUserController(
    private val securityService: SecurityService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val invisibleRecaptchaValidator: InvisibleRecaptchaValidator,
    private val recaptchaValidator: DefaultRecaptchaValidator,
    private val dtoValidator: DtoValidator,
    private val passwordService: RecoveryPasswordService,
    private val userTokenValidator: UserTokenValidator,
    private val emailValidator: EmailValidator
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

    @PostMapping("validateUserToken")
    fun validateUserToken(
        @Valid @RequestBody dto: UserTokenDto,
        result: BindingResult,
        response: HttpServletResponse
    ): ValidateResult {
        dtoValidator.validate(result)
        invisibleRecaptchaValidator.validate(dto.recaptcha)
        return userTokenValidator.validate(dto.token)
    }

    @PostMapping("saveRecoveryPassword")
    fun saveRecoveryPassword(
        @Valid @RequestBody command: SaveRecoveryPasswordCommand,
        result: BindingResult,
        response: HttpServletResponse
    ) {
        dtoValidator.validate(result)
        invisibleRecaptchaValidator.validate(command.recaptcha)
        userTokenValidator.validate(command.token)

        passwordService.changePassword(command)
            .let(jwtTokenProvider::createTokenByAuthentication)
            .let(jwtTokenProvider::getAuthenticationCookieByToken)
            .also(response::addCookie)
    }

    @PostMapping("recoveryPassword")
    fun recoveryPassword(
        @Valid @RequestBody command: PasswordRecoveryCommand,
        result: BindingResult
    ) {
        dtoValidator.validate(result)
        recaptchaValidator.validate(command.recaptcha)
        emailValidator.existsValidate(command.email)

        passwordService.recoveryByEmail(command.email)
    }
}
