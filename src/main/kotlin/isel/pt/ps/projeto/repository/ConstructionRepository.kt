package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.Construction
import isel.pt.ps.projeto.models.User


interface ConstructionRepository {
    fun getConstruction(oid: Int): Construction
    fun getConstructionsUsers(oid: Int): List<User>
}