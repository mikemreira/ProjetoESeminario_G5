package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.users.User

interface ConstructionRepository {
    fun getConstruction(oid: Int): Construction

    fun getConstructionsUsers(oid: Int): List<User>

    fun getConstructionsOfUser(id: Int): List<Construction>

    fun createConstruction(userId: Int, construction: Construction): Int

    fun checkConstructionByName(name: String): Boolean
}
