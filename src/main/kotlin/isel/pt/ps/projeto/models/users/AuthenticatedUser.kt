package isel.pt.ps.projeto.models.users

class AuthenticatedUser(
    val user: User,
    val token: String,
)
