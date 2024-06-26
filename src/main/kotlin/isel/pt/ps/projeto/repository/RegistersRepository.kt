package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import java.time.LocalDateTime


interface RegistersRepository {
    //fun getRegistersOfUser(uid: Int): List<Register>
    fun getUserRegisters(userId: Int): List<RegisterOutputModel>
    fun addUserRegisterEntry(userId: Int, obraId: Int, time: LocalDateTime) : Boolean
    fun addUserRegisterExit(userId: Int, obraId: Int, time: LocalDateTime) : Boolean
}