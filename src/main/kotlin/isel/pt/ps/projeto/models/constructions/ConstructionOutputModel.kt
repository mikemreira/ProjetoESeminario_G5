package isel.pt.ps.projeto.models.constructions

import kotlinx.datetime.LocalDate

data class ConstructionOutputModel(
    val oid: Int,
    val name: String,
    val location: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val thumbnailHref: String?,
    val infoListHref: String?,
    val status: String
)
