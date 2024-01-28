package ru.pmlite.api.security.validators

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.pmlite.api.security.domain.UserToken
import ru.pmlite.api.security.domain.UserTokenState
import ru.pmlite.api.security.dto.ValidateResult
import ru.pmlite.api.security.events.ConfirmedEmailEvent
import ru.pmlite.api.security.events.ExpiredTokenEvent
import ru.pmlite.api.security.exceptions.UserTokenValidException
import ru.pmlite.api.security.repositories.UserTokenRepository
import java.util.*

@Component
class UserTokenValidator(
    private val userTokenRepository: UserTokenRepository,
    private val publisher: ApplicationEventPublisher
) {
    fun validate(token: UUID): ValidateResult {
        return userTokenRepository.findTokenBy(token)
            ?.also(::checkExpiredState)
            ?.also(::checkEmailValidation)
            ?.takeIf(UserToken::isValid)

            ?.let { ValidateResult(true) }
            ?: throw UserTokenValidException("Токен недействителен")
    }

    private fun checkEmailValidation(token: UserToken) {
        publisher.publishEvent(ConfirmedEmailEvent(token.userId))
    }

    private fun checkExpiredState(token: UserToken) {
        if (token.isExpired && token.state == UserTokenState.PENDING)
            publisher.publishEvent(ExpiredTokenEvent(token.tokenId))
    }
}
