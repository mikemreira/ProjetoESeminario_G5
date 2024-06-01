package isel.pt.ps.projeto.controllers

import isel.pt.ps.projeto.controllers.pipeline.RequestTokenProcessor
import isel.pt.ps.projeto.models.Problem
import isel.pt.ps.projeto.models.registers.UserRegistersOutputModel
import isel.pt.ps.projeto.services.RegistosService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}