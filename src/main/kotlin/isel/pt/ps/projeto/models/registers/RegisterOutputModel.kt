package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime

data class RegisterOutputModel(
    val id: Int,
    val id_utilizador: Int,
    val id_obra: Int,
    val nome: String,
    val entrada: LocalDateTime,
    val saida: LocalDateTime? = null,
    val status: String? = null
)
