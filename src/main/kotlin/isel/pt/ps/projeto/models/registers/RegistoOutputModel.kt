package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime


data class RegistoOutputModel(
    val entrada: LocalDateTime,
    val saida: LocalDateTime? = null,
    val nome: String
)

data class RegistoInputModel(
    val time: LocalDateTime = LocalDateTime.now(),
    val obraId: Int
)

data class RegistoPostOutputModel(
    val message: String
)
