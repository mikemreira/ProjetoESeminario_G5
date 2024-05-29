package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.registers.Register

interface RegistersRepository {
    fun getRegistersOfUser(uid: Int): List<Register>
}