package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.registers.*
import isel.pt.ps.projeto.services.RegistersInfoError
import isel.pt.ps.projeto.services.RegistersService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping()
class RegistersController(
    private val registersService: RegistersService,
    private val requestTokenProcessor: RequestTokenProcessor,
) {

    @GetMapping("/registos")
    fun getUserRegisters(
        @RequestHeader("Authorization") userToken: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.getUserRegisters(authUser.user.id)
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
                                    it.nome,
                                    it.entrada,
                                    it.saida,
                                    it.status
                                )
                            }
                        )
                    )
            is Failure ->
                when (res.value) {
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoAccessToConstruction -> TODO()
                    RegistersInfoError.NoConstruction -> TODO()
                    RegistersInfoError.NoPermission -> TODO()
                }
        }
    }

    @PostMapping("/registos")
    fun addUsersRegisterEntry(
        @RequestHeader("Authorization") userToken: String,
        @RequestBody register: RegisterInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.addUserRegisterEntry(authUser.user.id, register.obraId, register.time)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.NoAccessToConstruction -> TODO()
                    RegistersInfoError.NoConstruction -> TODO()
                    RegistersInfoError.NoPermission -> TODO()
                }
        }
    }

    @PutMapping("/registos")
    fun addUsersRegisterExit(
        @RequestHeader("Authorization") userToken: String,
        @RequestBody register: RegisterInputModel
    ): ResponseEntity<*> {
        val authUser = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val res = registersService.addUserRegisterExit(authUser.user.id, register.obraId, register.time)
        return when (res) {
            is Success ->
                ResponseEntity.status(201)
                    .body(RegisterInfoModel("Successful"))
            is Failure ->
                when (res.value) {
                    RegistersInfoError.InvalidRegister -> Problem.response(400, Problem.invalidRegister)
                    RegistersInfoError.NoRegisters -> Problem.response(404, Problem.noRegisters)
                    RegistersInfoError.NoAccessToConstruction -> TODO()
                    RegistersInfoError.NoConstruction -> TODO()
                    RegistersInfoError.NoPermission -> TODO()
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
                RegistersInfoError.InvalidRegister -> TODO()
                RegistersInfoError.NoRegisters -> TODO()
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
            is Success -> ResponseEntity.status(200).body("Registo ${input.response}")
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> TODO()
                RegistersInfoError.NoRegisters -> TODO()
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
                })
            )
            is Failure -> when (res.value) {
                RegistersInfoError.NoConstruction -> Problem.response(404, Problem.constructionNotFound)
                RegistersInfoError.NoAccessToConstruction -> Problem.response(403, Problem.noConstructions)
                RegistersInfoError.NoPermission -> Problem.response(403, Problem.unauthorizedUser)
                RegistersInfoError.InvalidRegister -> TODO()
                RegistersInfoError.NoRegisters -> TODO()
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
                RegistersInfoError.InvalidRegister -> TODO()
                RegistersInfoError.NoRegisters -> TODO()
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
                RegistersInfoError.InvalidRegister -> TODO()
                RegistersInfoError.NoRegisters -> TODO()
            }
        }
    }
}