package ru.pmlite.api.security.validators

import org.springframework.stereotype.Service
import ru.pmlite.api.security.dto.ValidateResult
import ru.pmlite.api.security.exceptions.EmailValidException
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class EmailValidator(
    private val userAuthenticatedRepository: UserAuthenticatedRepository
) {
    fun notExistsValidate(email: String): ValidateResult {
        return if (userAuthenticatedRepository.existsByEmail(email)) throw EmailValidException()
        else ValidateResult(true)
    }

    fun existsValidate(email: String): ValidateResult {
        return if (userAuthenticatedRepository.existsByEmail(email)) ValidateResult(true)
        else throw EmailValidException()
    }
}
