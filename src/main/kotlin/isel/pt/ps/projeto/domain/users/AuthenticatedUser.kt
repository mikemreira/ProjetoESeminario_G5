package isel.pt.ps.projeto.domain.users

import isel.pt.ps.projeto.models.users.User

class AuthenticatedUser(
    val user: User,
    val token: String
)
