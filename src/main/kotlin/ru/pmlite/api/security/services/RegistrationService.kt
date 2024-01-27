package ru.pmlite.api.security.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import ru.pmlite.api.email.event.UserCreatedEvent
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.security.dto.RegistrationCommand
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class RegistrationService(
    private val userAuthenticatedRepository: UserAuthenticatedRepository,
    private val applicationPublisher: ApplicationEventPublisher
) {
    fun register(command: RegistrationCommand): UsernamePasswordAuthenticationToken {
        val userId = userAuthenticatedRepository.createUser(command)
        UserCreatedEvent(userId, command.name, command.email)
            .also(applicationPublisher::publishEvent)

        return UsernamePasswordAuthenticationToken(
            userId.id,
            "",
            listOf(SimpleGrantedAuthority(UserRole.ROLE_USER.name))
        )
    }



}
