package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.registers.Register
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import java.time.LocalDate
import java.time.LocalDateTime


interface RegistersRepository {
    //fun getRegistersOfUser(uid: Int): List<Register>
    fun getUserRegisters(userId: Int, pg: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterOutputModel>
    fun getUserRegistersSize(userId: Int, type: String, oid: Int?, forAdmin: Boolean, startDate: LocalDateTime?, endDate: LocalDateTime?): Int
    fun addUserRegisterEntry(userId: Int, obraId: Int, time: LocalDateTime, type: String) : Boolean
    fun addUserRegisterExit(regId: Int, userId: Int, obraId: Int, time: LocalDateTime) : Boolean
    fun addUserRegisterNFC(reg: Register?, userId: Int, obraId: Int, time: LocalDateTime, role: String) : Boolean
    fun insertExitOnWeb(userId: Int, regId: Int, obraId: Int, role: String, endTime: LocalDateTime): Boolean
    fun getUsersRegistersFromConstruction(oid: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser>
    fun getUserRegisterFromConstruction(userId: Int, oid: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser>
    fun getPendingRegistersFromConstruction(oid: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser>
    fun getPendingRegisters(userId: Int): List<RegisterAndUser>
    fun acceptOrDeny(userId: Int, oid: Int, registerId: Int, response: String) : Boolean
    fun getLatestEntryRegisterId(userId: Int, oid: Int): Register?
    fun getIncompleteRegistersFromConstruction(userId: Int, oid: Int, role: String, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterAndUser>
    fun getIncompleteRegisters(userId: Int, page: Int, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RegisterOutputModel>
    fun deleteRegister(userId: Int, obraId: Int, registerId: Int): Boolean
}