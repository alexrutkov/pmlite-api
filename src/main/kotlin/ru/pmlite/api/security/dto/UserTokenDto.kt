package ru.pmlite.api.security.dto

import jakarta.validation.constraints.NotBlank
import java.util.*

data class UserTokenDto(
    val token: UUID,
    @field:NotBlank
    val recaptcha: String
)
