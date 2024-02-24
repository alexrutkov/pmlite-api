package ru.pmlite.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Path
import java.nio.file.Paths

const val USER_HOME_PROPERTY = "user.home"
@Configuration
class FileSystemConfig {

    @Bean
    fun globalPath(
        @Value("\${spring.application.name}") applicationName: String
    ): Path = Paths.get(System.getProperty(USER_HOME_PROPERTY), applicationName)
}
