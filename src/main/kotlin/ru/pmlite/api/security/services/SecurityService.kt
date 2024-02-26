package ru.pmlite.api.security.services

import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.pmlite.api.security.domain.UserRole
import ru.pmlite.api.security.exceptions.NotMatchPasswordException
import ru.pmlite.api.security.providers.JwtTokenProvider
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository
import ru.pmlite.api.values.UserId

@Service
class SecurityService(
    private val authenticationManager: AuthenticationManager,
    private val repository: UserAuthenticatedRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Autowired
    var response: HttpServletResponse? = null
    fun authenticate(username: String, password: String): Authentication {
        return authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )
    }

    fun validatePassword(password: String) {
        val encodedPassword = repository.selectPassword(userId)
        if (!bCryptPasswordEncoder.matches(password, encodedPassword)) {
            throw NotMatchPasswordException()
        }
    }

    fun savePassword(password: String) {
        val encodedPassword = bCryptPasswordEncoder.encode(password)
        repository.updatePassword(userId, encodedPassword)
        UsernamePasswordAuthenticationToken(
            userId.id, "",
            repository.gelRolesByUser(userId).map { SimpleGrantedAuthority(it.name) }
        )
            .let(jwtTokenProvider::createTokenByAuthentication)
            .let(jwtTokenProvider::getAuthenticationCookieByToken)
            .also {response?.addCookie(it)}
    }

    val isAuthorized get() = SecurityContextHolder.getContext().authentication.isAuthenticated
    val userId get() = SecurityContextHolder.getContext().authentication.principal as UserId
    val roles get() = SecurityContextHolder.getContext().authentication
        .authorities.map { UserRole.valueOf(it.authority) }
}
