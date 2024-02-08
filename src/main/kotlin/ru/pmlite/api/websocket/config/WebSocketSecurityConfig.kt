package ru.pmlite.api.websocket.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager


@Configuration
@EnableWebSocketSecurity
class WebSocketSecurityConfig {

    @Bean
    fun messageAuthorizationManager(messages: MessageMatcherDelegatingAuthorizationManager.Builder): AuthorizationManager<Message<*>> {
        messages
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
            .anyMessage().permitAll()
        return messages.build()
    }

    @Bean(name = ["csrfChannelInterceptor"])
    fun csrfChannelInterceptor(): ChannelInterceptor {
        //TODO: переписать на рабочую версию Csrf
        return object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                return message
            }
        };
    }
}
