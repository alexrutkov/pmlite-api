package ru.pmlite.api.security.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationDtoException(override val message: String) : RuntimeException() {
}
