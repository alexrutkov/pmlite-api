package ru.pmlite.api.email.dto

data class SendEmailCommand(
    val email: String,
    val message: String,
    val subject: String
)
