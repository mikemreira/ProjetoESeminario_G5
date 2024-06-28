package isel.pt.ps.projeto.models.constructions

data class ConstructionInputModel (
    val name: String,
    val location: String,
    val description: String,
    val startDate: String,
    val endDate: String?,
    val foto: String?,
    val status: String?,
    val function: String,
)