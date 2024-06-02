package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.registers.RegistoInputModel
import isel.pt.ps.projeto.models.registers.RegistoPostOutputModel
import isel.pt.ps.projeto.models.registers.UserRegistersOutputModel
import isel.pt.ps.projeto.services.RegistosService
import jakarta.servlet.http.HttpServletResponse
import kotlinx.datetime.toKotlinLocalDateTime
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/registos")
class RegistosController(
    private val registosService: RegistosService,
    private val requestTokenProcessor: RequestTokenProcessor
) {
    @GetMapping("")
    fun getUserRegisters(@RequestHeader("Authorization") userToken: String, response: HttpServletResponse): ResponseEntity<*> {
        val user = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        val registers = registosService.getUserRegisters(user.user.id)
        return ResponseEntity.status(200).body(UserRegistersOutputModel(registers))
    }

    @PostMapping("")
    fun addUserRegisterEntrada(@RequestHeader("Authorization") userToken: String, @RequestBody register: RegistoInputModel): ResponseEntity<*> {
        val user = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        registosService.addUserRegisterEntrada(user.user.id, register.obraId, register.time.toKotlinLocalDateTime())
        return ResponseEntity.status(201).body(RegistoPostOutputModel("Successful"))
    }

    @PutMapping("")
    fun addUserRegisterSaida(@RequestHeader("Authorization") userToken: String, @RequestBody register: RegistoInputModel): ResponseEntity<*> {
        val user = requestTokenProcessor.processAuthorizationHeaderValue(userToken) ?: return Problem.response(401, Problem.unauthorizedUser)
        registosService.addUserRegisterSaida(user.user.id, register.obraId, register.time.toKotlinLocalDateTime())
        return ResponseEntity.status(201).body(RegistoPostOutputModel("Successful"))
    }
}