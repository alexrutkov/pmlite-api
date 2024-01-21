package ru.pmlite.api.security.services

import org.springframework.stereotype.Service
import ru.pmlite.api.security.dto.RegistrationCommand
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class RegistrationService(
    private val userAuthenticatedRepository: UserAuthenticatedRepository
) {
    fun register(command: RegistrationCommand) {
        userAuthenticatedRepository.createUser(command)
    }

}
