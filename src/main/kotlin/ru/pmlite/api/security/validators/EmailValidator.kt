package ru.pmlite.api.security.validators

import org.springframework.stereotype.Service
import ru.pmlite.api.security.dto.ValidateEmailCommand
import ru.pmlite.api.security.dto.ValidateResult
import ru.pmlite.api.security.exceptions.EmailValidException
import ru.pmlite.api.security.repositories.UserAuthenticatedRepository

@Service
class EmailValidator(
    private val userAuthenticatedRepository: UserAuthenticatedRepository
) {
    fun validate(command: ValidateEmailCommand): ValidateResult {
        return if (userAuthenticatedRepository.existsByEmail(command.email)) throw EmailValidException()
        else ValidateResult(true)
    }
}
