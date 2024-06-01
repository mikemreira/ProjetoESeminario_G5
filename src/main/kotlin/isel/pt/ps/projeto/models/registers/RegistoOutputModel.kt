package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime


data class RegistoOutputModel(
    val entrada: LocalDateTime,
    val saida: LocalDateTime? = null,
    val nome: String
)
