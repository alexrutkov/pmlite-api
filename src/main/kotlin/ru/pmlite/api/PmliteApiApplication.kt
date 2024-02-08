package ru.pmlite.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@ConfigurationPropertiesScan
@SpringBootApplication
class PmliteApiApplication

fun main(args: Array<String>) {
	runApplication<PmliteApiApplication>(*args)
}
