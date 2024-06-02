package pt.isel.projetoeseminario.model

import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

data class RegistoOutputModel(
    val entrada: LocalDateTime,
    val saida: LocalDateTime? = null,
    val nome: String
)

data class UserRegisterOutputModel(
    val registers: List<RegistoOutputModel>
)

data class RegistoPostOutputModel(
    val message: String
)

data class RegistoInputModel(
    val time: LocalDateTime,
    val obraId: Int
)
