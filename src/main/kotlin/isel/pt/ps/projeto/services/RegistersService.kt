package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.registers.ConstructionStatusAndUserRegisters
import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.repository.ConstructionRepository
import isel.pt.ps.projeto.repository.jdbc.RegistersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime

import org.springframework.stereotype.Component


sealed class RegistersInfoError {
    object NoRegisters : RegistersInfoError()
    object InvalidRegister : RegistersInfoError()
    object NoConstruction : RegistersInfoError()
    object NoPermission : RegistersInfoError()
    object NoAccessToConstruction : RegistersInfoError()
    object ConstructionSuspended : RegistersInfoError()
}

sealed class RegistersUserInfoError {
    object NoRegisters : RegistersUserInfoError()
    object InvalidRegister : RegistersUserInfoError()
}

typealias RegistersInfoResult = Either<RegistersUserInfoError, List<RegisterOutputModel>>
typealias ListOfUsersRegistersInfoResult = Either<RegistersInfoError, List<RegisterAndUser>>
typealias ListOfUsersRegistersAndConstructionStatusInfoResult = Either<RegistersInfoError, ConstructionStatusAndUserRegisters>
typealias EntryOrExitRegisterResult = Either<RegistersInfoError, Boolean>
typealias AcceptOrDenyRegisterResult = Either<RegistersInfoError, Boolean>

@Component
class RegistersService(
    private val registersRepository: RegistersRepository,
    private val constructionRepository: ConstructionRepository
) {

    fun getUserRegisters(uid: Int): RegistersInfoResult {

        val register = registersRepository.getUserRegisters(uid)
        return if (register.isEmpty()) {
            failure(RegistersUserInfoError.NoRegisters)
        } else {
            success(register)
        }
    }

    fun addUserRegisterEntry(uid: Int, obraId: Int, entry: LocalDateTime) : EntryOrExitRegisterResult {
        val construction = constructionRepository.getConstruction(obraId)
            ?: return failure(RegistersInfoError.NoConstruction)

        if (construction.status == "recoverable")
            return failure(RegistersInfoError.ConstructionSuspended)

        val res = registersRepository.addUserRegisterEntry(uid, obraId, entry.toJavaLocalDateTime())
        return if (res) {
            success(true)
        } else {
            failure(RegistersInfoError.InvalidRegister)
        }
    }

    fun addRegisterEntryByNFC(uid: Int, nfcId: String, entry: LocalDateTime) : EntryOrExitRegisterResult {
        val construction = constructionRepository.getConstructionByNFCID(nfcId)
            ?: return failure(RegistersInfoError.NoConstruction)

        if (construction.status == "recoverable")
            return failure(RegistersInfoError.ConstructionSuspended)

        val lastRegister = registersRepository.getLatestEntryRegisterId(uid, construction.oid)
        val res = if (lastRegister == null){
            registersRepository.addUserRegisterEntry(uid, construction.oid, entry.toJavaLocalDateTime())
        } else {
            registersRepository.addUserRegisterNFC(lastRegister, uid, construction.oid, entry.toJavaLocalDateTime())
        }

        return if (res) {
            success(true)
        } else {
            failure(RegistersInfoError.InvalidRegister)
        }
    }

    fun addUserRegisterExit(uid: Int, obraId: Int, exit: LocalDateTime) : EntryOrExitRegisterResult {

        val construction = constructionRepository.getConstruction(obraId)
            ?: return failure(RegistersInfoError.NoConstruction)

        if (construction.status == "recoverable")
            return failure(RegistersInfoError.ConstructionSuspended)

        val lastRegister = registersRepository.getLatestEntryRegisterId(uid, obraId)
            ?: return failure(RegistersInfoError.InvalidRegister)

        if (lastRegister.endTime != null)
            return failure(RegistersInfoError.InvalidRegister)

        val res = registersRepository.addUserRegisterExit(lastRegister.id, uid, obraId, exit.toJavaLocalDateTime())
        return if (res) {
            success(true)
        } else {
            failure(RegistersInfoError.InvalidRegister)
        }
    }

    fun getRegistersFromUsersInConstruction(userId: Int, oid: Int, page: Int): ListOfUsersRegistersAndConstructionStatusInfoResult {
        val construction = constructionRepository.getConstruction(oid)
            ?: return failure(RegistersInfoError.NoConstruction)

        val role = constructionRepository.getUserRoleFromConstruction(userId, construction.oid).also { println("ROLE : $it") }
            ?: return failure(RegistersInfoError.NoAccessToConstruction)

        if (role.role != "admin")
            return failure(RegistersInfoError.NoPermission)
        var pg = page
        if (page <= 0)
            pg = 1

        val registers = registersRepository.getUsersRegistersFromConstruction(oid, pg)
        val res = ConstructionStatusAndUserRegisters(constructionStatus = construction.status, registers = registers)
        return success(res)
    }

    fun getRegistersFromUserInConstruction(authId:Int, userId: Int, oid: Int, page: Int, me: Boolean): ListOfUsersRegistersInfoResult {
        val construction = constructionRepository.getConstruction(oid)
            ?: return failure(RegistersInfoError.NoConstruction)

        val role = constructionRepository.getUserRoleFromConstruction(authId, construction.oid).also { println("ROLE : $it") }
            ?: return failure(RegistersInfoError.NoAccessToConstruction)

        if (role.role != "admin" && !me)
            return failure(RegistersInfoError.NoPermission)

        var pg = page
        if (page <= 0)
            pg = 1

        val registers = registersRepository.getUserRegisterFromConstruction(userId, oid, pg)
        return success(registers)
    }

    fun getPendingRegistersFromUsersInConstruction(userId: Int, oid: Int, page: Int): ListOfUsersRegistersInfoResult {
        val construction = constructionRepository.getConstruction(oid)
            ?: return failure(RegistersInfoError.NoConstruction)

        val role = constructionRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(RegistersInfoError.NoAccessToConstruction)

        if (role.role != "admin")
            return failure(RegistersInfoError.NoPermission)

        var pg = page
        if (page <= 0)
            pg = 1

        val registers = registersRepository.getPendingRegistersFromConstruction(oid, pg)

        return success(registers)
    }

    fun getPendingRegistersFromUsers(userId: Int, page: Int): ListOfUsersRegistersInfoResult {
        var pg = page
        if (page <= 0)
            pg = 1
        val registers = registersRepository.getPendingRegisters(userId, pg)
        return success(registers)
    }

    fun acceptOrDenyRegisters(authId:Int, userId: Int, registerId: Int, oid: Int, response: String): AcceptOrDenyRegisterResult{
        val construction = constructionRepository.getConstruction(oid)
            ?: return failure(RegistersInfoError.NoConstruction)

        val role = constructionRepository.getUserRoleFromConstruction(authId, construction.oid).also { println("ROLE : $it") }
            ?: return failure(RegistersInfoError.NoAccessToConstruction)

        if (role.role != "admin")
            return failure(RegistersInfoError.NoPermission)

        val res = registersRepository.acceptOrDeny(userId, oid, registerId, response)
        return success(res)
    }

    fun getIncompleteRegisters(userId: Int): RegistersInfoResult {
        val res = registersRepository.getIncompleteRegisters(userId)
        return success(res)
    }

    fun insertExitOnWeb(userId: Int, registerId: Int, oid: Int, endTime: LocalDateTime): EntryOrExitRegisterResult {
        val construction = constructionRepository.getConstruction(oid)
            ?: return failure(RegistersInfoError.NoConstruction)

        val role = constructionRepository.getUserRoleFromConstruction(userId, construction.oid).also { println("ROLE : $it") }
            ?: return failure(RegistersInfoError.NoAccessToConstruction)

        val res = registersRepository.insertExitOnWeb(userId, registerId, oid, role.role, endTime.toJavaLocalDateTime())
        return success(res)
    }


}
