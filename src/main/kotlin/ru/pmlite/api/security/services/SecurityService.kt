package ru.pmlite.api.security.services

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.pmlite.api.values.UserId

@Service
class SecurityService {
    val isAuthorized get() = SecurityContextHolder.getContext().authentication.isAuthenticated
    val userId get() = SecurityContextHolder.getContext().authentication.principal as UserId
}
