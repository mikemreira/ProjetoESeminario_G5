package isel.pt.ps.projeto.models.constructions

import kotlinx.datetime.LocalDate

data class ConstructionAndRole(
    val construction: Construction,
    val role: String
)