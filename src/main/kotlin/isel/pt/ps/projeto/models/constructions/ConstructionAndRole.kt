package isel.pt.ps.projeto.models.constructions

import isel.pt.ps.projeto.models.role.Role
import kotlinx.datetime.LocalDate

data class ConstructionAndRole(
    val construction: Construction,
    val role: Role
)