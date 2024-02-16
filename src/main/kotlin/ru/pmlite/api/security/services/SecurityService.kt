package ru.pmlite.api.security.services

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.values.UserId

@Service
class SecurityService(
    private val authenticationManager: AuthenticationManager
) {
    fun authenticate(username: String, password: String): Authentication {
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )
    }

    val isAuthorized get() = SecurityContextHolder.getContext().authentication.isAuthenticated
    val userId get() = SecurityContextHolder.getContext().authentication.principal as UserId
    val roles get() = SecurityContextHolder.getContext().authentication
        .authorities.map { UserRole.valueOf(it.authority) }
}
