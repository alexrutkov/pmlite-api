package ru.pmlite.api.websocket.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import ru.pmlite.api.account.events.AccountEvent
import ru.pmlite.api.websocket.domains.WebsocketEvent


@Component
class WebSocketAccountEventListener(
    private val websocketTemplate: SimpMessagingTemplate,
    private val mapper: ObjectMapper
) {

    @EventListener
    fun handleWebSocketConnectListener(event: AccountEvent) {
        websocketTemplate.convertAndSendToUser(
            event.userId.toString(),
            "/topic/events",
            mapper.writeValueAsString(WebsocketEvent(event.type))
        )
    }
}
