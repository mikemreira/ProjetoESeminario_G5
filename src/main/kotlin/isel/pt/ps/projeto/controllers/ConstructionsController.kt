package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.casbin.AuthorizationService
import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.*
import isel.pt.ps.projeto.models.registers.RegisterInputModelWeb
import isel.pt.ps.projeto.models.users.ListOfSimpleUserAndFunc
import isel.pt.ps.projeto.models.users.SimpleUserAndFuncOutput
import isel.pt.ps.projeto.services.*
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// First controllera
@RestController
@RequestMapping("/obras")
@Profile("!test")
class ConstructionsController(
    private val constructionService: ConstructionsService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val authorizationService: AuthorizationService,
    private val utils: UtilsController
) {
    @GetMapping("/{oid}")
    fun getConstructionAndRole(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.getUserRoleOnConstruction(authUser.user.id, oid)
        return when (res) {
            is Success -> {
                val fotoString = if (res.value.construction.foto != null) utils.byteArrayToBase64(res.value.construction.foto) else null
                val result = res.value
                ResponseEntity.status(200)
                    .body(
                        ConstructionAndRoleOutputModel(
                            result.construction.oid,
                            result.construction.nome,
                            result.construction.localizacao,
                            result.construction.descricao,
                            result.construction.data_inicio,
                            result.construction.data_fim,
                            fotoString,
                            result.construction.status,
                            result.role.role,
                            result.role.function
                        )
                    )

            }
            is Failure ->
                when (res.value) {
                    ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                    ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                    ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                    ConstructionInfoError.NoAccessToConstruction -> Problem.response(401, Problem.unauthorizedUser)
                    ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    ConstructionInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
                }
        }
    }

    @GetMapping("/{oid}/nfc")
    fun getNfc(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.getNfc(authUser.user.id, oid)
        return when (res) {
            is Success -> {
                ResponseEntity.status(200)
                    .body(NfcOutputModel(res.value))
            }
            is Failure -> when (res.value) {
                NfcError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                NfcError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
            }
        }
    }

    @PutMapping("/{oid}/nfc")
    fun editNfc(
        @RequestHeader("Authorization") userToken: String,
        @RequestBody newNfc: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.editNfc(authUser.user.id, oid, newNfc)
        return when (res) {
            is Success -> {
                ResponseEntity.status(201)
                    .body(NfcOutputModel(res.value))
            }
            is Failure -> when (res.value) {
                NfcError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                NfcError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
            }
        }
    }

    @PutMapping("/{oid}")
    fun editConstruction(
        @RequestBody input: ConstructionEditInputModel,
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        println("INPUT : $input")
        val res = constructionService.editConstruction(authUser.user.id, oid, input)
        return when (res) {
            is Success -> {
                if (res.value == null)
                    ResponseEntity.status(204).body("deleted")
                else {
                    val fotoString = if (res.value.foto != null) utils.byteArrayToBase64(res.value.foto) else null
                    ResponseEntity.status(201).body(
                        ConstructionOutputModel(
                            res.value.oid,
                            res.value.nome,
                            res.value.localizacao,
                            res.value.descricao,
                            res.value.data_inicio,
                            res.value.data_fim,
                            fotoString,
                            res.value.status
                        )
                    )
                }
            }
            is Failure -> when (res.value) {
                ConstructionEditError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                ConstructionEditError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionEditError.InvalidInput -> Problem.response(400, Problem.invalidConstructionInput)

            }
        }
    }

    @GetMapping("/{oid}/users")
    fun getConstructionsUsers(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(
                401,
                Problem.unauthorizedUser
            )

        val res = constructionService.getConstructionUsers(authUser.user.id, oid, page)
        return when (res) {
            is Success -> {
                ResponseEntity.status(200)
                    .body(
                        ListOfSimpleUserAndFunc(
                            res.value.list.map {
                                val fotoString = if (it.foto != null) utils.byteArrayToBase64(it.foto) else null
                                SimpleUserAndFuncOutput(
                                    it.id,
                                    it.nome,
                                    it.email,
                                    it.morada,
                                    it.func,
                                    fotoString
                                )
                            },
                            res.value.size
                        )
                    )
            }

            is Failure -> when (res.value) {
                ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    @GetMapping("/{oid}/user/{uid}")
    fun getConstructionUser(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
        @PathVariable uid: Int
        ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.getConstructionUser(authUser.user.id, oid, uid)
        return when (res) {
            is Success -> {
                val fotoString = if (res.value.foto != null) utils.byteArrayToBase64(res.value.foto) else null
                ResponseEntity.status(200)
                    .body(
                        SimpleUserAndFuncOutput(
                            res.value.id,
                            res.value.nome,
                            res.value.email,
                            res.value.morada,
                            res.value.func,
                            fotoString
                        )
                    )
            }
            is Failure -> when (res.value) {
                ConstructionUserError.UserNotFound -> Problem.response(404, Problem.userNotFound)
                ConstructionUserError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
            }
        }
    }

    @DeleteMapping("/{oid}/user/{uid}")
    fun removeConstructionUser(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
        @PathVariable uid: Int,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = constructionService.removeConstructionUser(authUser.user.id, oid, uid)) {
            is Success -> ResponseEntity.status(204).body("deleted")
            is Failure -> when (res.value) {
                ConstructionUserError.UserNotFound -> Problem.response(404, Problem.userNotFound)
                ConstructionUserError.NoPermission -> Problem.response(403, Problem.userNotAdmin)
            }
        }
    }


    @GetMapping("")
    fun getConstructions(
        @RequestHeader("Authorization") userToken: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.getConstructionsOfUser(authUser.user.id, page)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        ListOfConstructionsOutputModel(
                            res.value.list.map {
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
                            },
                            res.value.size
                        )
                    )
            is Failure ->
                when (res.value) {
                    ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                    ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                    ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                    ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noAccessToConstruction)
                    ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    ConstructionInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
                }
        }
    }

    @GetMapping("/ongoing")
    fun getContructionsOnGoing(
            @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*> {
            val authUser =
                requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
            val res = constructionService.getOnGoingConstructionsOfUser(authUser.user.id)
            return when (res) {
                is Success ->
                    ResponseEntity.status(200)
                        .body(
                            ListOfConstructionsOutputModel(
                                res.value.list.map {
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
                                },
                                res.value.size
                            )
                        )
                is Failure ->
                    when (res.value) {
                        ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                        ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                        ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                        ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noAccessToConstruction)
                        ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                        ConstructionInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
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
            is Success -> {
                authorizationService.saveConstructionUserRole(authUser.user.email, res.value, "admin")
                authorizationService.createConstructionPolicies(res.value)
                authorizationService.enforcer()
                ResponseEntity.status(201)
                    .body(ConstructionIdOutputModel(res.value))
            }
            is Failure ->
                when (res.value) {
                    ConstructionCreationError.InvalidConstruction -> Problem.response(400, Problem.invalidConstruction)
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
                ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noAccessToConstruction)
                ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    @GetMapping("/isAdmin")
    fun isAdmin(
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = constructionService.isAdmin(authUser.user.id)
        return when (res) {
            is Success -> ResponseEntity.status(200).body(res.value)
            is Failure -> when (res.value) {
                IsAdminError.UserNotFound -> Problem.response(404, Problem.userNotFound)
            }
        }
    }

}
