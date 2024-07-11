package isel.pt.ps.projeto.models.users

data class SimpleUserAndFunc(
    val id: Int,
    val nome: String,
    val email: String,
    val morada: String?,
    val func: String?,
    val foto: ByteArray?
)