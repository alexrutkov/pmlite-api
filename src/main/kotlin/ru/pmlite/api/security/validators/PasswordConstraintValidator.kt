package ru.pmlite.api.security.validators

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.passay.*


class PasswordConstraintValidator : ConstraintValidator<ValidPassword, String> {
    override fun initialize(arg0: ValidPassword) {}
    override fun isValid(password: String, context: ConstraintValidatorContext): Boolean {
        val validator = PasswordValidator(
            listOf(
                LengthRule(8, 30),
                CharacterRule(EnglishCharacterData.LowerCase),
                CharacterRule(EnglishCharacterData.UpperCase),
                CharacterRule(EnglishCharacterData.Digit),
                CharacterRule(EnglishCharacterData.Special),
                IllegalSequenceRule(EnglishSequenceData.USQwerty),
                WhitespaceRule()
            )
        )
        val result: RuleResult = validator.validate(PasswordData(password))
        if (result.isValid) {
            return true
        }
        with(context) {
            disableDefaultConstraintViolation()
            buildConstraintViolationWithTemplate(
                validator.getMessages(result).joinToString()
            ).addConstraintViolation()
        }
        return false
    }
}
