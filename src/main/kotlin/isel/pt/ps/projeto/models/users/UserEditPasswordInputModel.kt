package isel.pt.ps.projeto.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UserEditPasswordInputModel (
    val password: String,
)