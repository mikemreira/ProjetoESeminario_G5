package isel.pt.ps.projeto.models.registers

import java.time.LocalDate

data class RegisterFilters(
    val mine: Boolean = true,
    val userId: Int?,
    val name: String?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val status: String?
)
