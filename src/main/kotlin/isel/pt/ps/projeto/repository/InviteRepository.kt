package isel.pt.ps.projeto.repository

import isel.pt.ps.projeto.models.users.SimpleUser

interface InviteRepository {

    fun getInvitedToConstruction(oid: Int) : List<SimpleUser>

    fun inviteToConstruction(userId: Int, oid: Int, email: String, function: String?): Boolean

}