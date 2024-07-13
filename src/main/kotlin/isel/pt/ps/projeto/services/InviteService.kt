package isel.pt.ps.projeto.services

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
    private val emailSenderService: EmailSenderService,
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

        val checkPresence = constructionsRepository.isUserAssociatedWithConstructionByEmail(oid, invite.email)
        if (checkPresence)
            return failure(InviteInfoError.AlreadyInConstruction)

        val res = inviteRepository.inviteToConstruction(oid, invite.email, invite.function, invite.role)

        var msg = "http://localhost:5173/"

        if (user == null)
            msg += "signup"

        emailSenderService.sendEmail(invite.email, "Convite para obra ${construction.nome}", msg)
        return success(res)
    }

    fun invited(email: String): ListOfInvitesInfoResult {
        val res = inviteRepository.invited(email)
        return success(res)
    }

    fun acceptOrDeny(email: String, oid: Int, response: String): InviteInfoResult{
        val res = inviteRepository.acceptOrDeny(email, oid, response)
        return success(res)
    }
}