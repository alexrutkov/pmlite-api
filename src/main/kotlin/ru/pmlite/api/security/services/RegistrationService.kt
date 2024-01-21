package ru.pmlite.api.security.services

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import ru.pmlite.api.security.dto.RegistrationCommand
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class RegistrationService(
    private val userAuthenticatedRepository: UserAuthenticatedRepository
) {
    fun register(command: RegistrationCommand): UsernamePasswordAuthenticationToken =
        UsernamePasswordAuthenticationToken(
            userAuthenticatedRepository.createUser(command),
            "",
            listOf(SimpleGrantedAuthority("USER"))
        )


}
