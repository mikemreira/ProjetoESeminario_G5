package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.users.User
import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import org.springframework.stereotype.Component

@Component
class ConstructionsService(private val constructionsRepository: ConstructionsRepository) {
    fun getConstruction(oid: Int): Construction = constructionsRepository.getConstruction(oid)

    fun getConstructionUsers(oid: Int): List<User> = constructionsRepository.getConstructionsUsers(oid)

    fun getConstructionsOfUser(id: Int): List<Construction> = constructionsRepository.getConstructionsOfUser(id)
}
