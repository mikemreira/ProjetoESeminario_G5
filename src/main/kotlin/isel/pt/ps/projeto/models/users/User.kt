package isel.pt.ps.projeto.models.users

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo

data class User(
    val id: Int,
    val nome: String,
    val email: String,
    val passwordValidation: PasswordValidationInfo,
    val morada: String?
)
