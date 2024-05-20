package isel.pt.ps.projeto.models.constructions

import kotlinx.datetime.LocalDate

data class ConstructionOutputModel(
    val oid: Int,
    val nome: String,
    val localizacao: String,
    val descricao: String,
    val data_inicio: LocalDate,
    val data_fim: LocalDate? = null,
    val status: String,
)
