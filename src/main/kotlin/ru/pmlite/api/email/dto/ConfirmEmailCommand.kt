package ru.pmlite.api.email.dto

data class ConfirmEmailCommand(
    val email: String,
    val name: String,

)
