package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime

data class RegisterByNfcInputModel (
    val time: LocalDateTime = LocalDateTime.now(),
    val nfcId: String
)