package ru.pmlite.api.security.listeners

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.security.domain.UserTokenState
import ru.pmlite.api.security.events.ConfirmedEmailEvent
import ru.pmlite.api.security.events.ExpiredTokenEvent
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository
import ru.pmlite.api.security.repositories.UserTokenRepository

@Component
class TokenValidationListener(
    private val userAuthenticatedRepository: UserAuthenticatedRepository,
    private val userTokenRepository: UserTokenRepository,
) {

    @Transactional @Async
    @EventListener(ConfirmedEmailEvent::class)
    fun emailValidationConfirm(event: ConfirmedEmailEvent) {
        userAuthenticatedRepository.confirmUserByEmail(event.userId)
    }

    @Transactional @Async
    @EventListener(ExpiredTokenEvent::class)
    fun expiredToken(event: ExpiredTokenEvent) {
        userTokenRepository.updateState(event.tokenId, UserTokenState.EXPIRED)
    }
}
