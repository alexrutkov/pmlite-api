package ru.pmlite.api.websocket.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val rabbitProperties: RabbitMqWebsocketProperties
) : WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocketApp").setAllowedOrigins("http://localhost:4200").withSockJS()
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
