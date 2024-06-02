package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.repository.jdbc.RegistersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success

import org.springframework.stereotype.Component
import java.time.LocalDateTime


sealed class RegistersInfoError {
    object NoRegisters : RegistersInfoError()
    object InvalidRegister : RegistersInfoError()
}
typealias RegistersInfoResult = Either<RegistersInfoError, List<RegisterOutputModel>>
typealias EntryRegisterResult = Either<RegistersInfoError, Boolean>

@Component
class RegistersService(
    private val registersRepository: RegistersRepository,

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


}