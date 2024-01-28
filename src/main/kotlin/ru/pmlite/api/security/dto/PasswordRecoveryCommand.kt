package ru.pmlite.api.security.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PasswordRecoveryCommand(
    @field: Email
    val email: String,
    @field: NotBlank
    val recaptcha: String
)
