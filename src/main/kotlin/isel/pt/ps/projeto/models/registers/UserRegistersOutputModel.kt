package isel.pt.ps.projeto.models.registers

data class UserRegistersOutputModel(
    val registers: List<RegisterOutputModel>,
    val pending: String?
)
