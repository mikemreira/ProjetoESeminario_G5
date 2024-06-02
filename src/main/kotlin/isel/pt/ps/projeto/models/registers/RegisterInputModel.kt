package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime

data class RegisterInputModel (
    val time: LocalDateTime = LocalDateTime.now(),
    val obraId: Int
)