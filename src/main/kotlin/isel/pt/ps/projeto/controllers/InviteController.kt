package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.domain.invite.Invite
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.ConstructionAndRoleOutputModel
import isel.pt.ps.projeto.services.ConstructionInfoError
import isel.pt.ps.projeto.services.ConstructionsService
import isel.pt.ps.projeto.services.InviteInfoError
import isel.pt.ps.projeto.services.InviteService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping()
class InviteController(
    private val inviteService: InviteService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val utils: UtilsController
) {

    @PostMapping("/obras/{oid}/invite")
    fun inviteUserToConstruction(
        @PathVariable oid: Int,
        @RequestBody invite: Invite,
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when(val res = inviteService.inviteToConstruction(authUser.user.id, oid, invite)) {
            is Success -> ResponseEntity.status(201)
                .body(res.value)
            is Failure -> when (res.value) {
                InviteInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                InviteInfoError.AlreadyInConstruction -> Problem.response(409, Problem.alreadyExists)
                InviteInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                InviteInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noAccessToConstruction)
                InviteInfoError.NoUserWithThatEmail -> Problem.response(404, Problem.userNotFound)
            }
        }
    }

}