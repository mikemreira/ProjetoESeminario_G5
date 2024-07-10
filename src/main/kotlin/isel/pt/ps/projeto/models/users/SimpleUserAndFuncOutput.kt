package isel.pt.ps.projeto.models.users

data class SimpleUserAndFuncOutput(
    val id: Int,
    val nome: String,
    val email: String,
    val morada: String?,
    val func: String?,
    val thumbnailHref: String?,
    val infoListHref: String?
)