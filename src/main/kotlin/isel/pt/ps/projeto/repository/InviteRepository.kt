package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.models.users.SimpleUser

interface InviteRepository {

    fun getInvitedToConstruction(oid: Int) : List<SimpleUser>
    fun inviteToConstruction(userId: Int, oid: Int, email: String, function: String, role: String): Boolean
    fun invited(userId: Int): List<ConstructionAndRole>
    fun acceptOrDeny(userId: Int, oid: Int, response: String): Boolean

}