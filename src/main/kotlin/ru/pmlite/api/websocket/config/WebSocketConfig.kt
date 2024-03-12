package ru.pmlite.api.websocket.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val rabbitProperties: RabbitMqWebsocketProperties,
    @Value("\${app.website.url}") private val websiteUrl: String
) : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocketApp").setAllowedOrigins(websiteUrl).withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost(rabbitProperties.host)
                .setRelayPort(rabbitProperties.port)
                .setClientLogin(rabbitProperties.user)
                .setClientPasscode(rabbitProperties.password)
                .setSystemLogin(rabbitProperties.user)
                .setSystemPasscode(rabbitProperties.password)

    }
}
