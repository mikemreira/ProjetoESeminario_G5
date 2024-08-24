package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.registers.Register
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import java.time.LocalDate
import java.time.LocalDateTime


interface RegistersRepository {
    //fun getRegistersOfUser(uid: Int): List<Register>
    fun getUserRegisters(userId: Int, pg: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterOutputModel>
    fun addUserRegisterEntry(userId: Int, obraId: Int, time: LocalDateTime) : Boolean
    fun addUserRegisterExit(regId: Int, userId: Int, obraId: Int, time: LocalDateTime) : Boolean
    fun addUserRegisterNFC(reg: Register?, userId: Int, obraId: Int, time: LocalDateTime) : Boolean
    fun insertExitOnWeb(userId: Int, regId: Int, obraId: Int, role: String, endTime: LocalDateTime): Boolean
    fun getUsersRegistersFromConstruction(oid: Int, page: Int): List<RegisterAndUser>
    fun getUserRegisterFromConstruction(userId: Int, oid: Int, page: Int): List<RegisterAndUser>
    fun getPendingRegistersFromConstruction(oid: Int, page: Int): List<RegisterAndUser>
    fun getPendingRegisters(userId: Int, page: Int): List<RegisterAndUser>
    fun acceptOrDeny(userId: Int, oid: Int, registerId: Int, response: String) : Boolean
    fun getLatestEntryRegisterId(userId: Int, oid: Int): Register?
    fun getIncompleteRegisters(userId: Int): List<RegisterOutputModel>
    fun deleteRegister(userId: Int, obraId: Int, registerId: Int): Boolean
}