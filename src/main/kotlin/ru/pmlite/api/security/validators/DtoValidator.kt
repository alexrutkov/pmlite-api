package ru.pmlite.api.security.validators

import org.springframework.stereotype.Service
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import ru.pmlite.api.security.exceptions.ValidationDtoException

@Service
class DtoValidator {
    fun validate(result: BindingResult) {
        if (result.hasErrors()) {
            throw result.allErrors.filterIsInstance<FieldError>()
                .joinToString { "${it.field}: ${it.defaultMessage}" }
                .let(::ValidationDtoException)
        }
    }
}
