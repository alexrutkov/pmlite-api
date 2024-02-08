package ru.pmlite.api.websocket.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app.rabbitmq")
data class RabbitMqWebsocketProperties(
    val host: String,
    val port: Int,
    val user: String,
    val password: String
)
