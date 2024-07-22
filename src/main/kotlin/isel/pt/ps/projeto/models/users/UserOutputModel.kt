package isel.pt.ps.projeto.models.users

data class UserOutputModel(
    val id: Int,
    val nome: String,
    val email: String,
    val morada: String?,
    val fotoHref: String?,
)
