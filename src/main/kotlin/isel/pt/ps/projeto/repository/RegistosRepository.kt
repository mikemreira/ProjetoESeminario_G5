package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.registers.RegistoOutputModel
import kotlinx.datetime.LocalDateTime

interface RegistosRepository {
    fun getUserRegisters(userId: Int): List<RegistoOutputModel>
    fun addUserRegisterEntrance(userId: Int, obraId: Int, time: LocalDateTime)
    fun addUserRegisterSaida(userId: Int, obraId: Int, time: LocalDateTime)
}