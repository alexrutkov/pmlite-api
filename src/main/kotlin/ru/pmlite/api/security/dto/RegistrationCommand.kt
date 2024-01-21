package ru.pmlite.api.security.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegistrationCommand(
    @field: Size(max = 100, min = 2)
    val name: String,
    @field: Email
    val email: String,
    @field:NotBlank
    val recaptcha: String
)
