package isel.pt.ps.projeto.models.constructions

import java.time.LocalDate

data class ConstructionEditInputModel (
    val name: String,
    val location: String,
    val description: String,
    val startDate: String?,
    val endDate: String?,
    val foto: String?,
    val status: String?,
    val function: String
)