package isel.pt.ps.projeto.models.registers

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RegisterInputModelWeb(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
