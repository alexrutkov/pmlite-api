package ru.pmlite.api.remotes.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.CookieManager
import java.net.http.HttpClient
import java.time.Duration

@Configuration
class RemotesConfig {
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient.newBuilder()
            .cookieHandler(CookieManager())
            .connectTimeout(Duration.ofSeconds(30))
            .build()
    }
}
