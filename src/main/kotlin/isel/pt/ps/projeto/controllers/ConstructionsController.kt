package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.constructions.*
import isel.pt.ps.projeto.models.registers.RegisterQuery
import isel.pt.ps.projeto.models.registers.RegisterInputModelWeb
import isel.pt.ps.projeto.models.users.ListOfSimpleUserAndFunc
import isel.pt.ps.projeto.models.users.SimpleUserAndFunc
import isel.pt.ps.projeto.models.users.SimpleUserAndFuncOutput
import isel.pt.ps.projeto.services.*
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
   // private val authorizationService: AuthorizationService,
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

        val res = constructionService.getUserRoleOnConstruction(authUser.user.id, oid)

        // add everything in a new OutputModel with role and construction
        return when (res) {
            is Success -> {
                val fotoString = if (res.value.construction.foto != null) utils.byteArrayToBase64(res.value.construction.foto) else null
                val result = res.value
                ResponseEntity.status(200)
                    .body(
                        ConstructionOutputModelAndHrefs(
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
                            ),
                            "/obras/${result.construction.oid}/users",
                            "/obras/${result.construction.oid}/registos",
                            "/obras/${result.construction.oid}/edit"
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
                    ConstructionInfoError.ConstructionSuspended -> TODO()
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
        val res = constructionService.editConstruction(authUser.user.id, oid, input)
        return when (res) {
            is Success -> {
                if (res.value == null)
                    ResponseEntity.status(204).body("Deleted")
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
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(
                401,
                Problem.unauthorizedUser
            )

        val res = constructionService.getConstructionUsers(authUser.user.id, oid)
        //return ResponseEntity.status(200).body(users)
        return when (res) {
            is Success -> {
                ResponseEntity.status(200)
                    .body(
                        ListOfSimpleUserAndFunc(
                            res.value.map {
                                val fotoString = if (it.foto != null) utils.byteArrayToBase64(it.foto) else null
                                SimpleUserAndFuncOutput(
                                    it.id,
                                    it.nome,
                                    it.email,
                                    it.morada,
                                    it.func,
                                    fotoString
                                )
                            }
                        )
                    )
            }

            is Failure -> when (res.value) {
                ConstructionInfoError.ConstructionNotFound -> Problem.response(404, Problem.constructionNotFound)
                ConstructionInfoError.NoConstructions -> Problem.response(404, Problem.noConstructions)
                ConstructionInfoError.EmptyEmployees -> Problem.response(400, Problem.emptyEmployees)
                ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                ConstructionInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionInfoError.AlreadyInConstruction -> TODO()
                ConstructionInfoError.ConstructionSuspended -> TODO()
            }
        }
    }

    @GetMapping("/{oid}/user/{uid}")
    fun getConstructionUser(
        @RequestHeader("Authorization") userToken: String,
        @PathVariable oid: Int,
        @PathVariable uid: Int,
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
                    ConstructionInfoError.ConstructionSuspended -> TODO()
                }
        }
    }

    @GetMapping("/obras/ongoing")
    fun getContructionsOnGoing(
            @RequestHeader("Authorization") userToken: String
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
                        ConstructionInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noAccessToConstruction)
                        ConstructionInfoError.InvalidRegister -> TODO()
                        ConstructionInfoError.NoPermission -> TODO()
                        ConstructionInfoError.AlreadyInConstruction -> TODO()
                        ConstructionInfoError.ConstructionSuspended -> TODO()
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
                ConstructionInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                ConstructionInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                ConstructionInfoError.AlreadyInConstruction -> TODO()
                ConstructionInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

}
