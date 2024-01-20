package ru.pmlite.api.security.dto

data class CreateTokenCommand(
    val username: String,
    val password: String
)
