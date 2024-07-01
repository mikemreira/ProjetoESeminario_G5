package isel.pt.ps.projeto.models.constructions

import kotlinx.datetime.LocalDate

data class ConstructionAndRoleOutputModel(
    val oid: Int,
    val name: String,
    val location: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val foto: String?,
    val status: String?,
    val role: String,
    val function: String?
)
