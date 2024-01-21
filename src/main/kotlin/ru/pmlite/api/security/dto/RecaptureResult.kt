package ru.pmlite.api.security.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class RecaptureResult(
    @JsonProperty("challenge_ts")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val challengeTs: LocalDateTime?,
    @JsonProperty("error-codes")
    val errorCodes: List<String>?,
    val hostname: String?,
    val success: Boolean
)
