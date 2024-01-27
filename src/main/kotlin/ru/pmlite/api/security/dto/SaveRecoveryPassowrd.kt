package ru.pmlite.api.security.dto

import jakarta.validation.constraints.NotBlank
import ru.pmlite.api.security.validators.ValidPassword

data class SaveRecoveryPasswordCommand(
    @field:NotBlank
    val token: String,
    @field:[ValidPassword NotBlank]
    val password: String,
    @field:NotBlank
    val recaptcha: String
)
