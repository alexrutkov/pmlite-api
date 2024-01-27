package ru.pmlite.api.security.validators

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.pmlite.api.security.dto.ValidateResult

@Service
class InvisibleRecaptchaValidator(
    @Value("\${app.recaptcha.invisible.secret-key}")
    private val secretKey: String,
    private val recaptchaValidator: RecaptchaValidator
) {
    fun validate(recaptcha: String): ValidateResult  = recaptchaValidator.validate(recaptcha, secretKey)

}
