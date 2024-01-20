package ru.pmlite.api.security.dto

data class UserAuthenticatedDetails(
    val email: String,
    val password: String,
    val id: Long
)
