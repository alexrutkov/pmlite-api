package ru.pmlite.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PmliteApiApplication

fun main(args: Array<String>) {
	runApplication<PmliteApiApplication>(*args)
}
