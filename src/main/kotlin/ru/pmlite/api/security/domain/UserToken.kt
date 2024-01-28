package ru.pmlite.api.security.domain

import ru.pmlite.api.values.TokenId
import ru.pmlite.api.values.UserId
import java.time.Instant

data class UserToken(
    val expiredAt: Instant,
    val state: UserTokenState,
    val type: UserTokenType,
    val tokenId: TokenId,
    val userId: UserId
) {
    val isValid get() = expiredAt > Instant.now() && state == UserTokenState.PENDING
    val isExpired get() = expiredAt < Instant.now()
}
