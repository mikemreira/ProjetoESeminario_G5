package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.casbin.AuthorizationService
import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.invite.Invite
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.ConstructionAndRoleOutputModel
import isel.pt.ps.projeto.models.constructions.ListOfConstructionAndRoleOutputModel
import isel.pt.ps.projeto.models.invite.AcceptOrDenyInviteModel
import isel.pt.ps.projeto.services.ConstructionsService
import isel.pt.ps.projeto.services.InviteInfoError
import isel.pt.ps.projeto.services.InviteService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
@Profile("!test")
class InviteController(
    private val inviteService: InviteService,
    private val constructionsService: ConstructionsService,
    private val authorizationService: AuthorizationService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val utils: UtilsController
) {

    @PostMapping("/obras/{oid}/convite")
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

    @GetMapping("/convites")
    fun getInvites(
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when(val res = inviteService.invited(authUser.user.email)) {
            is Success -> ResponseEntity.status(200)
                .body(
                    ListOfConstructionAndRoleOutputModel(
                        res.value.map {
                            ConstructionAndRoleOutputModel(
                                it.construction.oid,
                                it.construction.nome,
                                it.construction.localizacao,
                                it.construction.descricao,
                                it.construction.data_inicio,
                                it.construction.data_fim,
                                if (it.construction.foto != null) utils.byteArrayToBase64(it.construction.foto) else null,
                                it.construction.status,
                                it.role.role,
                                it.role.function
                            )
                        }
                    )
                )
            is Failure -> when (res.value) {
                InviteInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                InviteInfoError.AlreadyInConstruction -> Problem.response(409, Problem.alreadyExists)
                InviteInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                InviteInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noAccessToConstruction)
                InviteInfoError.NoUserWithThatEmail -> Problem.response(404, Problem.userNotFound)
            }
        }
    }


    @PutMapping("/convites")
    fun acceptOrDeny(
        @RequestBody input: AcceptOrDenyInviteModel,
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)

        val res = inviteService.acceptOrDeny(authUser.user.email, input.oid, input.response)
        return when(res) {
            is Success -> {
                println("ADDED USER ${authUser.user.id}")
                authorizationService.saveConstructionUserRole(authUser.user.email, input.oid, res.value)
                ResponseEntity.status(201)
                    .body("O Convite foi ${input.response}")
            }

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