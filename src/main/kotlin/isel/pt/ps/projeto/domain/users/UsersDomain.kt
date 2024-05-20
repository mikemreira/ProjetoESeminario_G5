package isel.pt.ps.projeto.domain.users

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UsersDomain(
    private val passwordEncoder: PasswordEncoder,
) {
    fun validatePassword(
        password: String,
        validationInfo: PasswordValidationInfo,
    ) = passwordEncoder.matches(
        password,
        validationInfo.validationInfo,
    )

    fun createPasswordValidationInformation(password: String) =
        PasswordValidationInfo(
            validationInfo = passwordEncoder.encode(password),
        )

    fun isSafePassword(password: String) =
        password.length > 5 && containsDigits(password) && containsUpperCase(password) && containsLowerCase(password)

    fun containsDigits(input: String): Boolean {
        return input.any { it.isDigit() }
    }

    fun containsUpperCase(input: String): Boolean {
        return input.any { it.isUpperCase() }
    }

    fun containsLowerCase(input: String): Boolean {
        return input.any { it.isLowerCase() }
    }
}
