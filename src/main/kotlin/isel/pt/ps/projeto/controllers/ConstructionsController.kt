package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.*
import isel.pt.ps.projeto.models.registers.RegisterQuery
import isel.pt.ps.projeto.models.registers.RegisterInputModelWeb
import isel.pt.ps.projeto.services.ConstructionCreationError
import isel.pt.ps.projeto.services.ConstructionInfoError
import isel.pt.ps.projeto.services.ConstructionsService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/obras")
class ConstructionsController(
    private val constructionService: ConstructionsService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val utils: UtilsController
) {
    @GetMapping("/{oid}")
    fun getConstructionAndRole(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        // add process to get user by token
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        // TODO(Find a way to return null and then the error maybe ?)

        val res = constructionService.getUserRoleOnConstruction(authUser.token, oid)

        // add everything in a new OutputModel with role and construction
        return when (res) {
            is Success -> {
                val fotoString = if (res.value.construction.foto != null) utils.byteArrayToBase64(res.value.construction.foto) else null
                ResponseEntity.status(200)
                    .body(
                        ConstructionAndRoleOutputModel(
                            res.value.construction.nome,
                            res.value.construction.localizacao,
                            res.value.construction.descricao,
                            res.value.construction.data_inicio,
                            res.value.construction.data_fim,
                            fotoString,
                            res.value.construction.status,
                            res.value.role
                        )
                    )
            }
            is Failure ->
                when (res.value) {
                    ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                    ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                    ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                    ConstructionInfoError.NoAccessToConstruction -> Problem.response(401, Problem.unauthorizedUser)
                    ConstructionInfoError.InvalidRegister -> TODO()
                    ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    ConstructionInfoError.AlreadyInConstruction -> TODO()
                }
        }
    }

    @GetMapping("/{oid}/users")
    fun getConstructionsUsers(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)

        val users = constructionService.getConstructionUsers(authUser.user.id, oid)
        return ResponseEntity.status(200).body(users)
    }

    @GetMapping("")
    fun getConstructions(
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.getConstructionsOfUser(authUser.user.id)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        ListOfConstructionsOutputModel(
                            res.value.map {
                                val fotoString = if (it.foto != null) utils.byteArrayToBase64(it.foto) else null
                                ConstructionOutputModel(
                                    it.oid,
                                    it.nome,
                                    it.localizacao,
                                    it.descricao,
                                    it.data_inicio,
                                    it.data_fim,
                                    fotoString,
                                    it.status
                                )
                            }
                        )
                    )
            is Failure ->
                when (res.value) {
                    ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                    ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                    ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                    ConstructionInfoError.NoAccessToConstruction -> TODO()
                    ConstructionInfoError.InvalidRegister -> TODO()
                    ConstructionInfoError.NoPermission -> TODO()
                    ConstructionInfoError.AlreadyInConstruction -> TODO()
                }
        }
    }

    @PostMapping("")
    fun createConstruction(
        @RequestBody input: ConstructionInputModel,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)

        val res = constructionService.createConstruction(
            authUser.user.id,
            input.name,
            input.location,
            input.description,
            input.startDate.toLocalDate(),
            input.endDate?.toLocalDate(),
            input.foto,
            input.status,
            input.function
        )
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(ConstructionIdOutputModel(res.value))
            is Failure ->
                when (res.value) {
                    //ConstructionCreationError.ConstructionAlreadyExists -> Problem.response(400, Problem.constructionAlreadyExists)
                    ConstructionCreationError.InvalidConstruction -> Problem.response(400, Problem.invalidConstruction)
                }
        }
    }
/*
    @PostMapping("{oid}/invite")
    fun inviteUserToConstruction(
        @PathVariable oid: Int,
        @RequestBody invite: Invite,
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.inviteToConstruction(authUser.user.id, oid, invite)
        // TODO(ERROR)
    }

 */

    @GetMapping("{oid}/registers")
    fun getRegistersOfUserFromConstruction(
        @PathVariable oid: Int,
        @RequestBody filters: RegisterQuery,
        @RequestParam page: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        filters.page = page
        return when (val res = constructionService.getRegisters(authUser.user.id, oid, filters)) {
            is Success -> ResponseEntity.status(200).body(res.value)
            is Failure -> when (res.value) {
                ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                ConstructionInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionInfoError.AlreadyInConstruction -> TODO()
            }
        }
    }

    @PostMapping("{oid}/register")
    fun registerInConstruction(
        @RequestBody input: RegisterInputModelWeb,
        @PathVariable oid: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.registerIntoConstruction(
            authUser.user.id,
            oid,
            input.startTime.toLocalDateTime(),
            input.endTime.toLocalDateTime()
        )
        return when(res) {
            is Success -> ResponseEntity.status(201).body("Registered")
            is Failure -> when (res.value) {
                ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                ConstructionInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionInfoError.AlreadyInConstruction -> TODO()
            }
        }
    }

}
