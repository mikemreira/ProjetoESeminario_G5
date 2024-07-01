package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.registers.RegisterAndUser
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.repository.ConstructionRepository
import isel.pt.ps.projeto.repository.jdbc.RegistersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success

import org.springframework.stereotype.Component
import java.time.LocalDateTime


sealed class RegistersInfoError {
    object NoRegisters : RegistersInfoError()
    object InvalidRegister : RegistersInfoError()
    object NoConstruction : RegistersInfoError()
    object NoPermission : RegistersInfoError()
    object NoAccessToConstruction : RegistersInfoError()
}
typealias RegistersInfoResult = Either<RegistersInfoError, List<RegisterOutputModel>>
typealias ListOfUsersRegistersInfoResult = Either<RegistersInfoError, List<RegisterAndUser>>
typealias EntryRegisterResult = Either<RegistersInfoError, Boolean>
typealias AcceptOrDenyRegisterResult = Either<RegistersInfoError, Boolean>

@Component
class RegistersService(
    private val registersRepository: RegistersRepository,
    private val constructionRepository: ConstructionRepository
) {

    fun getUserRegisters(uid: Int): RegistersInfoResult {
        val register = registersRepository.getUserRegisters(uid)
        return if (register.isEmpty()) {
            failure(RegistersInfoError.NoRegisters)
        } else {
            success(register)
        }
    }

    fun addUserRegisterEntry(uid: Int, obraId: Int, entry: LocalDateTime) : EntryRegisterResult {
        val res = registersRepository.addUserRegisterEntry(uid, obraId, entry)
        return if (res) {
            success(true)
        } else {
            failure(RegistersInfoError.InvalidRegister)
        }
    }

    fun addUserRegisterExit(uid: Int, obraId: Int, exit: LocalDateTime) : EntryRegisterResult {
        val res = registersRepository.addUserRegisterExit(uid, obraId, exit)
        return if (res) {
            success(true)
        } else {
            failure(RegistersInfoError.InvalidRegister)
        }
    }

    fun getRegistersFromUsersInConstruction(userId: Int, oid: Int, page: Int): ListOfUsersRegistersInfoResult {
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

        return success(registers)
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

}
