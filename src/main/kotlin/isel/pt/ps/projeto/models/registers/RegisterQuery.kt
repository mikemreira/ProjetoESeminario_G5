package isel.pt.ps.projeto.models.registers

import java.time.LocalDate

data class RegisterQuery(
    val me: Boolean = false,
    val userId: Int? = null,
    var page: Int = 0
)
