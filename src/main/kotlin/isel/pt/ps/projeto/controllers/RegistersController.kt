package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.registers.*
import isel.pt.ps.projeto.services.RegisterDeleteError
import isel.pt.ps.projeto.services.RegistersInfoError
import isel.pt.ps.projeto.services.RegistersService
import isel.pt.ps.projeto.services.RegistersUserInfoError
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import jakarta.servlet.http.HttpServletResponse
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping()
class RegistersController(
    private val registersService: RegistersService,
    private val requestTokenProcessor: RequestTokenProcessor,
    private val utils: UtilsController
) {

    @GetMapping("/registos")
    fun getUserRegisters(
        @RequestHeader("Authorization") userToken: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam initialDate: String?,
        @RequestParam endDate: String?,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.getUserRegisters(authUser.user.id, page, initialDate, endDate)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        UserRegistersOutputModel(
                            res.value.map {
                                RegisterOutputModel(
                                    it.id,
                                    it.id_utilizador,
                                    it.id_obra,
                                    it.nome_obra,
                                    it.entrada,
                                    it.saida,
                                    it.status
                                )
                            },
                            "/registos/pendente"
                        )
                    )
            is Failure ->
                when (res.value) {
                    RegistersUserInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersUserInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersUserInfoError.InvalidParams -> TODO()
                }
        }
    }

    @PostMapping("/registos")
    fun addUsersRegisterEntry(
        @RequestHeader("Authorization") userToken: String,
        @RequestBody register: RegisterInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.addUserRegisterEntry(authUser.user.id, register.obraId, register.time.toKotlinLocalDateTime())
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                    RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                    RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
                }
        }
    }

    @PostMapping("/registos/nfc")
    fun addRegisterEntryByNfc(
        @RequestHeader("Authorization") userToken: String,
        @RequestBody register: RegisterByNfcInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        println("Registo NFC")
        val res = registersService.addRegisterEntryByNFC(authUser.user.id, register.nfcId, register.time.toKotlinLocalDateTime())
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                    RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                    RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
                }
        }
    }

    @PutMapping("/registos")
    fun addUsersRegisterExit(
        @RequestHeader("Authorization") userToken: String,
        @RequestBody register: RegisterInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.addUserRegisterExit(authUser.user.id, register.obraId, register.time.toKotlinLocalDateTime())
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                    RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                    RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
                }
        }
    }

    // ver registos de uma obra, apenas admin
    @GetMapping("/obras/{oid}/registos")
    fun getRegistersOfUsersFromConstruction(
        @PathVariable oid: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = registersService.getRegistersFromUsersInConstruction(authUser.user.id, oid, page)) {
            is Success -> ResponseEntity.status(200).body(
                UserRegistersAndObraOutputModel(
                    res.value.registers.map {
                        RegisterAndUser(
                            it.userName,
                            it.id,
                            it.oid,
                            it.uid,
                            it.startTime,
                            it.endTime,
                            it.status
                        )
                    },
                    res.value.constructionStatus
                    ,
                    "${utils.path}/obras/$oid/registos/me"
                    ,
                    "${utils.path}/obras/$oid/registos/pendente"
                    ,
                    "${utils.path}/obras/$oid/registos"
                )
            )
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    @PutMapping("/obras/{oid}/registos")
    fun acceptOrDenyRegistersFromConstruction(
        @PathVariable oid: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestBody input: AcceptOrDenyRegister,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = registersService.acceptOrDenyRegisters(authUser.user.id, input.userId, input.registerId, oid, input.response)) {
            is Success -> ResponseEntity.status(201).body("Registo ${input.response}")
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    // ver registos de um utilizador numa obra, apenas admin
    @GetMapping("/obras/{oid}/registos/{userId}")
    fun getRegistersOfUserFromConstruction(
        @PathVariable oid: Int,
        @PathVariable userId: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = registersService.getRegistersFromUserInConstruction(authUser.user.id, userId, oid, page,false)) {
            is Success -> ResponseEntity.status(200).body(
                UserRegistersAndObraOutputModel(
                    res.value.map {
                        RegisterAndUser(
                            it.userName,
                            it.id,
                            it.oid,
                            it.uid,
                            it.startTime,
                            it.endTime,
                            it.status
                        )
                    }
                )
            )
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    // ver os meus registos numa obra
    @GetMapping("/obras/{oid}/registos/me")
    fun getRegistersMyRegistersFromConstruction(
        @PathVariable oid: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = registersService.getRegistersFromUserInConstruction(authUser.user.id, authUser.user.id, oid, page, true)) {
            is Success -> ResponseEntity.status(200).body(
                UserRegistersAndObraOutputModel(
                res.value.map {
                    RegisterAndUser(
                        it.userName,
                        it.id,
                        it.oid,
                        it.uid,
                        it.startTime,
                        it.endTime,
                        it.status
                    )
                }
            ))
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    @GetMapping("/obras/{oid}/registos/pendente")
    fun getPendingRegistersOfUsersFromConstruction(
        @PathVariable oid: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = registersService.getPendingRegistersFromUsersInConstruction(authUser.user.id, oid, page)) {
            is Success -> ResponseEntity.status(200).body(
                UserRegistersAndObraOutputModel(
                    res.value.map {
                        RegisterAndUser(
                            it.userName,
                            it.id,
                            it.oid,
                            it.uid,
                            it.startTime,
                            it.endTime,
                            it.status
                        )
                    })
            )
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }

    @GetMapping("/registos/pendente")
    fun getPendingRegistersOfUsers(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestHeader("Authorization") userToken: String,
    ): ResponseEntity<*>{
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        return when (val res = registersService.getPendingRegistersFromUsers(authUser.user.id, page)) {
            is Success -> ResponseEntity.status(200).body(
                UserRegistersAndObraOutputModel(
                    res.value.map {
                        RegisterAndUser(
                            it.userName,
                            it.id,
                            it.oid,
                            it.uid,
                            it.startTime,
                            it.endTime,
                            it.status
                        )
                    })
            )
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
            }
        }
    }
    @GetMapping("/registos/incompletos")
    fun getRegisterWithoutExit(
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*> {
        val authUser =
            requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.getIncompleteRegisters(authUser.user.id)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(
                        UserRegistersOutputModel(
                            res.value.map {
                                RegisterOutputModel(
                                    it.id,
                                    it.id_utilizador,
                                    it.id_obra,
                                    it.nome_obra,
                                    it.entrada,
                                    it.saida,
                                    it.status
                                )
                            },
                            null
                        )
                    )
            is Failure ->
                when (res.value) {
                    RegistersUserInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersUserInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersUserInfoError.InvalidParams -> TODO()
                }
        }
    }

    @PutMapping("/registos/incompletos")
    fun insertExitOnWeb(
        @RequestBody input: ExitWebInputModel,
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*> {
        val authUser
            = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.insertExitOnWeb(authUser.user.id, input.registerId, input.oid, input.endTime.toLocalDateTime())
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                    RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                    RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                    RegistersInfoError.ConstructionSuspended -> Problem.response(403, Problem.constructionSuspended)
                }
        }
    }

    @DeleteMapping("/obras/{oid}/registos/{rid}")
    fun deleteRegister(
        @PathVariable oid: Int,
        @PathVariable rid: Int,
        @RequestHeader("Authorization") userToken: String
    ): ResponseEntity<*> {
        val authUser
            = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.deleteRegister(authUser.user.id, oid, rid)
        return when (res) {
            is Success ->
                ResponseEntity.status(200)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegisterDeleteError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                }
        }
    }
}