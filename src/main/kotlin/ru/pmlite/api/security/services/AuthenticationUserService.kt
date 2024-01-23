package ru.pmlite.api.security.services

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class AuthenticationUserService(
    private val userAuthenticatedRepository: UserAuthenticatedRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = this.userAuthenticatedRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Пользователь $username не найден!")
        return User(user.id.toString(), user.password, listOf(SimpleGrantedAuthority(UserRole.ROLE_USER.name)))
    }
}
