package pt.isel.projetoeseminario.model

import java.time.LocalDate

data class Obra(
    val oid: Int,
    val nome: String,
    val localizacao: String,
    val descricao: String,
    val data_inicio: LocalDate,
    val data_fim: LocalDate? = null,
    val foto: Byte? = null,
    val status: String
)

data class ObrasOutputModel(
    val obras: List<Obra>
)
