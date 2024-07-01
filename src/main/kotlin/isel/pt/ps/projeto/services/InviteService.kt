package isel.pt.ps.projeto.services

import isel.pt.ps.projeto.domain.constructions.ConstructionsDomain
import isel.pt.ps.projeto.models.invite.Invite
import isel.pt.ps.projeto.models.constructions.ConstructionAndRole
import isel.pt.ps.projeto.repository.InviteRepository
import isel.pt.ps.projeto.repository.jdbc.ConstructionsRepository
import isel.pt.ps.projeto.repository.jdbc.UsersRepository
import isel.pt.ps.projeto.utils.Either
import isel.pt.ps.projeto.utils.failure
import isel.pt.ps.projeto.utils.success
import org.springframework.stereotype.Component

sealed class InviteInfoError {
    object ConstructionNotFound : InviteInfoError()
    object NoAccessToConstruction: InviteInfoError()
    object NoPermission: InviteInfoError()
    object NoUserWithThatEmail: InviteInfoError()
    object AlreadyInConstruction: InviteInfoError()
}

typealias InviteInfoResult = Either<InviteInfoError, Boolean>
typealias ListOfInvitesInfoResult = Either<InviteInfoError, List<ConstructionAndRole>>


@Component
class InviteService(
    private val constructionsRepository: ConstructionsRepository,
    private val usersRepository: UsersRepository,
    private val inviteRepository: InviteRepository
) {

    fun inviteToConstruction(userId: Int, oid: Int, invite: Invite): InviteInfoResult {
        val construction = constructionsRepository.getConstruction(oid)
            ?: return failure(InviteInfoError.ConstructionNotFound)

        val role = constructionsRepository.getUserRoleFromConstruction(userId, construction.oid)
            ?: return failure(InviteInfoError.NoAccessToConstruction)

        if (role.role != "admin")
            return failure(InviteInfoError.NoPermission)

        val user = usersRepository.getUserByEmail(invite.email)
            ?: return failure(InviteInfoError.NoUserWithThatEmail)

        val checkPresence = constructionsRepository.getUserByEmailFromConstructions(oid, invite.email)

        if (checkPresence != null)
            return failure(InviteInfoError.AlreadyInConstruction)

        // TODO(SEND MAIL)

        val res = inviteRepository.inviteToConstruction(user.id, oid, invite.email, invite.function)
        return success(res)
    }

    fun invited(userId: Int): ListOfInvitesInfoResult {
        val res = inviteRepository.invited(userId)
        return success(res)
    }

    fun acceptOrDeny(userId: Int, oid: Int, response: String): InviteInfoResult{
        val res = inviteRepository.acceptOrDeny(userId, oid, response)
        return success(res)
    }
}