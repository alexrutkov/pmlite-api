package ru.pmlite.api.security.events

import ru.pmlite.api.values.TokenId

data class ExpiredTokenEvent(
    val tokenId: TokenId
)
