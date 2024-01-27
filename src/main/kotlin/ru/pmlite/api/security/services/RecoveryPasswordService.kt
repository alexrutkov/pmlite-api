package ru.pmlite.api.security.services

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class RecoveryPasswordService(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val authenticatedRepository: UserAuthenticatedRepository
) {
}
