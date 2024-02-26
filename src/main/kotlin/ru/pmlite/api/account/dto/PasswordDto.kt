package ru.pmlite.api.account.dto

import jakarta.validation.constraints.NotBlank
import ru.pmlite.api.security.validators.ValidPassword

data class PasswordDto(
    @field:[ValidPassword NotBlank]
    val password: String
)
