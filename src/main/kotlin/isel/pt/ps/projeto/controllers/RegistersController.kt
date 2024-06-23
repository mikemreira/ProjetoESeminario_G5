package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.registers.RegisterInfoModel
import isel.pt.ps.projeto.models.registers.RegisterInputModel
import isel.pt.ps.projeto.models.registers.RegisterOutputModel
import isel.pt.ps.projeto.models.registers.UserRegistersOutputModel
import isel.pt.ps.projeto.services.RegistersInfoError
import isel.pt.ps.projeto.services.RegistersService
import isel.pt.ps.projeto.utils.Failure
import isel.pt.ps.projeto.utils.Success
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/registos")
class RegistersController(
    private val registersService: RegistersService,
    private val requestTokenProcessor: RequestTokenProcessor,
) {

    @GetMapping("")
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
                }
        }
    }

    @PostMapping("")
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
                }
        }
    }

    @PutMapping("")
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
                }
        }
    }
}