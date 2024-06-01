package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.users.AuthenticatedUser
import isel.pt.ps.projeto.models.registers.RegistoOutputModel
import isel.pt.ps.projeto.repository.RegistosRepository
import org.springframework.stereotype.Component

@Component
class RegistosService(
    private val registosRepository: RegistosRepository
) {
    fun getUserRegisters(id: Int): List<RegistoOutputModel> {
        return registosRepository.getUserRegisters(id)
    }
}