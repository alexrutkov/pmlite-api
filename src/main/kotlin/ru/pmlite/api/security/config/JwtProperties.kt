package ru.pmlite.api.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@ConfigurationProperties(prefix = "app.jwt")
data class JwtProperties(
    var secretKey: String = "",
    var validityInMs: Duration = Duration.ofDays(30)
)
