package isel.pt.ps.projeto.models.registers

data class UserRegistersAndObraOutputModel(
    val registers: List<RegisterAndUser>,
    val constructionStatus: String? = null,
    val meRoute: String? = null,
    val pendingRoute: String? = null,
    val allRoute: String? = null
)