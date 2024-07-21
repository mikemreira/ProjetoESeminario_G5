package isel.pt.ps.projeto.models.users

data class UserOutputModel(
    val id: Int,
    val nome: String,
    val email: String,
    val morada: String?,
    val foto: String?,
    val thumbnailHref: String?,
    val listHref: String?
)
