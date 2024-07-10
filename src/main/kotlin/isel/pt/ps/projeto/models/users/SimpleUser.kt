package isel.pt.ps.projeto.models.users

import isel.pt.ps.projeto.domain.users.PasswordValidationInfo

data class SimpleUser(
    val id: Int,
    val nome: String,
    val email: String,
    val morada: String?,
)