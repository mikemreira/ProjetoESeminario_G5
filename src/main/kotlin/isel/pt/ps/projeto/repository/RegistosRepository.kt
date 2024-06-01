package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.registers.RegistoOutputModel

interface RegistosRepository {
    fun getUserRegisters(id: Int): List<RegistoOutputModel>
}