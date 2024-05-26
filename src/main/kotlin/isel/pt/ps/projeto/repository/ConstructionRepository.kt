package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.constructions.Construction
import isel.pt.ps.projeto.models.users.User
import kotlinx.datetime.LocalDate

interface ConstructionRepository {
    fun getConstruction(oid: Int): Construction

    fun getConstructionsUsers(oid: Int): List<User>

    fun getConstructionsOfUser(id: Int): List<Construction>

    fun createConstruction(userId: Int, name: String, location: String, description: String, startDate: LocalDate, endDate: LocalDate?, foto: String?, status: String?): Int
    fun getUserRoleFromConstruction(id: Int, oid: Int) : String
    fun checkConstructionByName(name: String): Boolean
}
