package isel.pt.ps.projeto.models.users

import kotlinx.serialization.Serializable

@Serializable
class AuthenticatedUser(
    val user: User,
    val token: String,
)
