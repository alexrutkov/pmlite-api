package ru.pmlite.api.security.validators

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.pmlite.api.security.domain.UserToken
import ru.pmlite.api.security.domain.UserTokenState
import ru.pmlite.api.security.domain.UserTokenType
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
            ?.takeIf(UserToken::isValid)
            ?.also {
                if (it.type == UserTokenType.EMAIL_VALIDATION) {
                    publisher.publishEvent(ConfirmedEmailEvent(it.userId))
                }
            }
            ?.let { ValidateResult(true) }
            ?: throw UserTokenValidException("Токен недействителен")
    }

    private fun checkExpiredState(token: UserToken) {
        if (token.isExpired && token.state != UserTokenState.EXPIRED)
            publisher.publishEvent(ExpiredTokenEvent(token.tokenId))
    }
}
