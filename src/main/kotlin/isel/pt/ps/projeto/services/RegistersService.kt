package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.registers.Register
import isel.pt.ps.projeto.repository.jdbc.RegistersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import org.springframework.stereotype.Component


sealed class RegistersInfoError {
    object NoRegisters : RegistersInfoError()

}
typealias RegistersInfoResult = Either<RegistersInfoError, List<Register>>

@Component
class RegistersService(
    private val registersRepository: RegistersRepository,

    ) {
    fun getRegistersOfUser(uid: Int): RegistersInfoResult {
        val register = registersRepository.getRegistersOfUser(uid)
        return if (register.isEmpty()) {
            failure(RegistersInfoError.NoRegisters)
        } else {
            success(register)
        }
    }




}