package isel.pt.ps.projeto.models.registers

data class ConstructionStatusAndUserRegisters(
    val registers: List<RegisterAndUser>,
    val constructionStatus: String
)
