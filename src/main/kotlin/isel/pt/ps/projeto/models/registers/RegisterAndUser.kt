package isel.pt.ps.projeto.models.registers

import java.time.LocalDateTime

data class RegisterAndUser(
    val userName: String,
    val id: Int,
    val oid: Int,
    val uid: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val status: String,
)
