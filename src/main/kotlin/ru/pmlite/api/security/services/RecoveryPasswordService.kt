package ru.pmlite.api.security.services

import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.pmlite.api.email.event.UserRecoveryRequestEvent
import ru.pmlite.api.email.services.EmailService
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.security.domain.UserTokenState
import ru.pmlite.api.security.dto.SaveRecoveryPasswordCommand
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository
import ru.pmlite.api.security.repositories.UserTokenRepository
import ru.pmlite.api.values.TokenId

@Service
class RecoveryPasswordService(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val authenticatedRepository: UserAuthenticatedRepository,
    private val tokenRepository: UserTokenRepository,
    private val publisher: ApplicationEventPublisher,
    private val emailService: EmailService
) {
    @Transactional
    fun changePassword(command: SaveRecoveryPasswordCommand): Authentication {
        val userId = tokenRepository.findTokenBy(command.token)?.userId ?: throw UsernameNotFoundException("Пользователь не найден!")
        authenticatedRepository.updatePassword(
            userId, bCryptPasswordEncoder.encode(command.password)
        )
        tokenRepository.updateState(TokenId(command.token), UserTokenState.CONFIRMED)
        return UsernamePasswordAuthenticationToken(
            userId.id,
            "",
            authenticatedRepository.gelRolesByUser(userId)
                .map { SimpleGrantedAuthority(it.name) }
                .plus(SimpleGrantedAuthority(UserRole.ROLE_USER.name))
        )
    }

    fun recoveryByEmail(email: String) {
        authenticatedRepository.findUserDetailsByEmail(email)
            .also { user ->
                publisher.publishEvent(
                    UserRecoveryRequestEvent(user.userId, user.email, user.name)
                )
            }
    }
}
