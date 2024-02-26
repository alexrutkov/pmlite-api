package ru.pmlite.api.account.dto

import jakarta.validation.constraints.NotBlank
import ru.pmlite.api.security.validators.ValidPassword

data class SavePasswordCommand(
    @field:[ValidPassword NotBlank]
    val oldPassword: String,
    @field:[ValidPassword NotBlank]
    val password: String
)
