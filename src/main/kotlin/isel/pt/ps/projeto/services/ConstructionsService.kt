package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.Construction
import isel.pt.ps.projeto.models.User
import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import org.springframework.stereotype.Component

@Component
class ConstructionsService(private val constructionsRepository: ConstructionsRepository) {
    fun getConstruction(oid: Int): Construction = constructionsRepository.getConstruction(oid)
    fun getConstructionUsers(oid: Int): List<User> = constructionsRepository.getConstructionsUsers(oid)
}