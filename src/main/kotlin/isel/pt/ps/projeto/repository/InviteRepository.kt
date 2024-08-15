package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.models.users.SimpleUser

interface InviteRepository {

    //fun getInvitedToConstruction(oid: Int) : List<SimpleUser>
    fun inviteToConstruction(oid: Int, email: String, function: String, role: String): Boolean
    fun invited(email: String): List<ConstructionAndRole>
    fun acceptOrDeny(email: String, oid: Int, response: String): Boolean

}