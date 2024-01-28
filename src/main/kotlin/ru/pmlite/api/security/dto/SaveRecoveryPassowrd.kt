package ru.pmlite.api.security.dto

import jakarta.validation.constraints.NotBlank
import ru.pmlite.api.security.validators.ValidPassword
import java.util.*

data class SaveRecoveryPasswordCommand(
    val token: UUID,
    @field:[ValidPassword NotBlank]
    val password: String,
    @field:NotBlank
    val recaptcha: String
)
