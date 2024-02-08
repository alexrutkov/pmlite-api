package ru.pmlite.api.websocket.domains

import ru.pmlite.api.account.domains.AccountEventType

data class WebsocketEvent(
    val type: AccountEventType
)
