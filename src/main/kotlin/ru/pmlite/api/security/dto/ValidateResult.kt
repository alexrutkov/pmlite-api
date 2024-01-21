package ru.pmlite.api.security.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ValidateResult(
    @JsonProperty("isValid") val isValid: Boolean
)
